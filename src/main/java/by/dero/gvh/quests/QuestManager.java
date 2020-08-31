package by.dero.gvh.quests;

import java.util.UUID;

public interface QuestManager {
	default void inc(UUID user, String category) {
		push(user, category, 1);
	}
	
	void push(UUID user, String category, int value);
}
