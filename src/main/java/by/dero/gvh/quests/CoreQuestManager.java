package by.dero.gvh.quests;

import ru.cristalix.core.CoreApi;
import ru.cristalix.core.level.ILevelService;
import ru.cristalix.core.level.LevelService;
import ru.cristalix.core.message.IMessageService;
import ru.cristalix.core.message.MessageService;
import ru.cristalix.core.network.ISocketClient;
import ru.cristalix.core.quest.IQuestService;
import ru.cristalix.core.quest.QuestInstance;
import ru.cristalix.core.quest.QuestService;

import java.util.*;

public class CoreQuestManager implements QuestManager {
	private final Map<String, List<QuestInstance>> questsMap = new HashMap<>();
	
	public CoreQuestManager(String type) {
		CoreApi.get().registerService(IMessageService.class, new MessageService(ISocketClient.get()));
		CoreApi.get().registerService(ILevelService.class, new LevelService(ISocketClient.get()));
		CoreApi.get().registerService(IQuestService.class, new QuestService(CoreApi.get().getPlatform(), ISocketClient.get(), type));
		registerExpQuest("kills", "AETH-D-0", "Убить 50 человек", 2500);
		registerExpQuest("damage", "AETH-D-1", "Набрать 3000 очков захвата", 2500);
		registerExpQuest("spell_damage", "AETH-D-2", "Нанести способностями 2000 урона", 2500);
		registerExpQuest("capture", "AETH-D-3", "Набрать 3000 очков захвата", 2500);
//		registerExpQuest("damage", "AETH-D-4", "Нанести способностями 2000", 15000);
		registerExpQuest("kills", "AETH-W-0", "Убить 250 человек", 15000);
		registerExpQuest("capture", "AETH-W-1", "Набрать 15000 очков захвата", 15000);
		registerExpQuest("spell_damage", "AETH-W-2", "Нанести способностями 10000 урона", 15000);
		registerExpQuest("advancement", "AETH-W-3", "Набрать 5000 очков полезности", 15000);
		registerExpQuest("damage", "AETH-W-4", "Нанести 30000 урона", 15000);
	}
	
	private void registerExpQuest(String category, String questId, String questName, int experience) {
		questsMap.computeIfAbsent(category, __ -> new ArrayList<>())
				.add(IQuestService.get().makeQuest(questId, IQuestService.experienceCallback(questName, experience)));
	}
	
	@Override
	public void push(UUID user, String category, int value) {
		Optional.ofNullable(questsMap.get(category)).ifPresent(list -> list.forEach(inst -> inst.progress(user, value)));
	}
}
