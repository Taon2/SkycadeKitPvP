package net.skycade.kitpvp.stat;

import net.skycade.kitpvp.kit.KitType;
import org.bson.Document;

public class KitData {

	private final Document document;

	public KitData(KitType kitType) {
		this(new Document());
		setLevel(1);
	}

	public KitData(Document document) {
		this.document = document;
	}

	public int getLevel() {
		return getInt("level");
	}

	public void setLevel(int level) {
		set("level", level);
	}

	public int getXp() {
        return getInt("xp");
    }

    public void setXp(int xp) {
	    set("xp", xp);
    }

	private int getInt(String key) {
		if (!document.containsKey(key))
			document.put(key, 0);
		return document.getInteger(key);
	}

	private void set(String key, Object value) {
		document.put(key, value);
	}

	public Document getDocument() {
		return document;
	}

}