package fr.hdelaunay.image;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class AppSettings {
	
	@SerializationOptions(name = "last-files")
	public List<String> lastFiles = new ArrayList<String>();
	
	private transient File file;
	
	public AppSettings(final File file) {
		this.file = file;
	}
	
	public final File getFile() {
		return file;
	}
	
	public final void setFile(final File file) {
		this.file = file;
	}
	
	public final void load() throws IOException, IllegalArgumentException, IllegalAccessException {
		if(!file.exists()) {
			this.save();
			return;
		}
		boolean needToSave = false;
		final JsonObject object = Json.parse(Files.readAllLines(Paths.get(file.getPath()), StandardCharsets.UTF_8).get(0)).asObject();
		for(final Field field : this.getClass().getFields()) {
			final SerializationOptions options = this.getAnnotation(field);
			if(options == null) {
				continue;
			}
			final Class<?> type = field.getType();
			final JsonValue value = object.get(options.name());
			if(value == null) {
				needToSave = true;
				continue;
			}
			if(Collection.class.isAssignableFrom(type)) {
				final List<String> list = new ArrayList<String>();
				for(final JsonValue line : value.asArray()) {
					list.add(line.asString());
				}
				field.set(this, list);
			}
		}
		if(needToSave) {
			save();
		}
	}
	
	public final void save() throws IOException, IllegalArgumentException, IllegalAccessException {
		final JsonObject object = new JsonObject();
		for(final Field field : this.getClass().getFields()) {
			final SerializationOptions options = this.getAnnotation(field);
			if(options == null) {
				continue;
			}
			final Class<?> type = field.getType();
			if(Collection.class.isAssignableFrom(type)) {
				final JsonArray array = new JsonArray();
				for(final Object value : (ArrayList<?>)field.get(this)) {
					array.add(value.toString());
				}
				object.add(options.name(), array);
			}
		}
		if(file.exists()) {
			file.delete();
		}
		Files.write(Paths.get(file.getPath()), object.toString().getBytes());
	}
	
	private final SerializationOptions getAnnotation(final Field field) {
		if(Modifier.isTransient(field.getModifiers())) {
			return null;
		}
		final SerializationOptions options = field.getAnnotation(SerializationOptions.class);
		return options;
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	private @interface SerializationOptions {
		
		public String name();
		
	}
	
}
