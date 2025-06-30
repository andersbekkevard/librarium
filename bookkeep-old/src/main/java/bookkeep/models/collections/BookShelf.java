package bookkeep.models.collections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookShelf implements Serializable {
	private String name;
	private final List<UUID> bookUUIDs;

	public BookShelf(String name) {
		this.name = name;
		bookUUIDs = new ArrayList<>();
	}

	public void addId(UUID id) {
		bookUUIDs.add(id);
	}

	public void removeId(UUID id) {
		bookUUIDs.remove(id);
	}

	public boolean contains(UUID id) {
		return bookUUIDs.contains(id);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<UUID> getUUIDs() {
		return bookUUIDs;
	}

	@Override
	public String toString() {
		return "{" + name + ", size=" + bookUUIDs.size() + "}";
	}

}
