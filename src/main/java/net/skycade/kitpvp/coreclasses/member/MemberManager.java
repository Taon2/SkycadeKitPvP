package net.skycade.kitpvp.coreclasses.member;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import net.skycade.kitpvp.coreclasses.commands.Module;
import net.skycade.kitpvp.coreclasses.member.listeners.MemberJoinQuit;
import net.skycade.kitpvp.coreclasses.member.listeners.MemberListeners;
import net.skycade.kitpvp.stat.KitPvPDB;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class MemberManager extends Module {

    private static MemberManager instance;

    private final Map<UUID, Member> members = new HashMap<>();

    private MemberManager() {
        registerListener(new MemberJoinQuit(this));
        registerListener(new MemberListeners(this));
    }

	public Map<UUID, Member> getMembers() {
		return members;
	}

	public MongoCollection<Document> getCollection() {
		return KitPvPDB.getInstance().getMemberCollection();
	}

    public Member getMember(Player p, boolean database) {
        return getMember(p.getUniqueId(), database);
    }

    public Member getMember(Player p) {
        return getMember(p.getUniqueId());
    }

    public Member getMember(UUID uuid) {
        return getMember(uuid, false);
    }

    public Member getOfflineMember(UUID uuid) {
        return getMember(uuid, true);
    }

    public Member getMember(UUID uuid, boolean database) {
        if (members.containsKey(uuid))
            return members.get(uuid);
        if (database) {
            FindIterable<Document> result = getCollection().find(new Document("uuid", uuid.toString()));
            if (result.first() != null) {
                Member member = new Member(result.first());
                members.put(uuid, member);
                return member;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public Member getMember(String name, boolean database) {
        for (Member member : members.values())
            if (member.getName().equalsIgnoreCase(name))
                return member;
        if (database) {
            if (name.length() < 3 && !name.equalsIgnoreCase("G") && !name.equalsIgnoreCase("F")
                    && !name.equalsIgnoreCase("8"))
                return null;
            FindIterable<Document> result = getCollection()
                    .find(new Document("previous_names", new Document("$regex", "(?i)" + name)));
            for (Document document : result)
                if (document.getString("name").equalsIgnoreCase(name))
                    return new Member(document);
            for (Document document : result)
                for (String s : (List<String>) document.get("previous_names"))
                    if (s.equalsIgnoreCase(name))
                        return new Member(document);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
	public Member getMember(String name) {
		for (Member member : members.values())
			if (member.getName().equalsIgnoreCase(name))
				return member;

        if (name.length() < 3 && !name.equalsIgnoreCase("G") && !name.equalsIgnoreCase("F")
                && !name.equalsIgnoreCase("8"))
            return null;
        FindIterable<Document> result = getCollection()
                .find(new Document("previous_names", new Document("$regex", "(?i)" + name)));
        for (Document document : result)
            if (document.getString("name").equalsIgnoreCase(name))
                return new Member(document);
        for (Document document : result) {
            for (String s : (List<String>) document.get("previous_names")) {
                if (s.equalsIgnoreCase(name)) {
                    Member member = new Member(document);
                    members.put(member.getUUID(), member);
                }
            }
        }
		return null;
	}

	public void update(Member member) {
		new BukkitRunnable() {
			public void run() {
				getCollection().updateOne(new Document("_id", member.getDocument().get("_id")),
						new Document("$set", member.getDocument()));
			}
		}.runTaskAsynchronously(getPlugin());
	}

	public void onDisable() {
		for (Map.Entry<UUID, Member> entry : members.entrySet()) {
			Member member = entry.getValue();
			getCollection().updateOne(new Document("_id", member.getDocument().get("_id")), new Document("$set", member.getDocument()));
		}
	}

    public static MemberManager getInstance() {
		if (instance == null)
			instance = new MemberManager();
		return instance;
	}

}