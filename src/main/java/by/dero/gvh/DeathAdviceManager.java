package by.dero.gvh;

import by.dero.gvh.model.Lang;

import java.util.*;

public class DeathAdviceManager {
    public DeathAdviceManager() {
        // all
        register("all", Lang.get("death.all.collectors"), 100);
        register("all", Lang.get("death.all.upgrade"), 100);
        register("all", Lang.get("death.all.unlock"), 100);
        register("all", Lang.get("death.all.vk"), 100);
        register("all", Lang.get("death.all.win"), 100);
        register("all", Lang.get("death.all.help"), 100);
        register("all", Lang.get("death.all.lift"), 100);
        register("all", Lang.get("death.all.space"), 100);
        register("all", Lang.get("death.all.bug"), 100);
        register("all", Lang.get("death.all.advice"), 100);
        // lucifer
        register("lucifer", Lang.get("death.lucifer.swap"), 100);
        register("lucifer", Lang.get("death.lucifer.space"), 100);
        register("lucifer", Lang.get("death.lucifer.sword"), 100);
        register("lucifer", Lang.get("death.lucifer.suicideJump"), 100);
        // assassin
        register("assassin", Lang.get("death.assassin.ninjarope"), 100);
        register("assassin", Lang.get("death.assassin.ninjarope2"), 100);
        register("assassin", Lang.get("death.assassin.space"), 100);
        register("assassin", Lang.get("death.assassin.dagger"), 100);
        register("assassin", Lang.get("death.assassin.smokes"), 100);
        // warrior
        register("warrior", Lang.get("death.warrior.webthrow"), 100);
        register("warrior", Lang.get("death.warrior.healall"), 100);
        register("warrior", Lang.get("death.warrior.space"), 100);
        register("warrior", Lang.get("death.warrior.sword"), 100);
        // dovahkiin
        register("dovahkiin", Lang.get("death.dovahkiin.dragon"), 100);
        register("dovahkiin", Lang.get("death.dovahkiin.pearls"), 100);
        register("dovahkiin", Lang.get("death.dovahkiin.space"), 100);
        register("dovahkiin", Lang.get("death.dovahkiin.sword"), 100);
        // thor
        register("thor", Lang.get("death.thor.stunall"), 100);
        register("thor", Lang.get("death.thor.hammer"), 100);
        register("thor", Lang.get("death.thor.space"), 100);
        register("thor", Lang.get("death.thor.chainlightning"), 100);
        register("thor", Lang.get("death.thor.chainlightning2"), 100);
        // horseman
        register("horseman", Lang.get("death.horseman.bow"), 100);
        register("horseman", Lang.get("death.horseman.axe"), 100);
        register("horseman", Lang.get("death.horseman.space"), 100);
        register("horseman", Lang.get("death.horseman.horse"), 100);
        register("horseman", Lang.get("death.horseman.meteor"), 100);
    }

    public static class Advice {
        public Advice(String text, int chance, int id) {
            this.text = text;
            this.chance = chance;
            this.id = id;
        }

        String text;
        int chance;
        int id;
    }

    private final HashMap<String, ArrayList<Advice>> advices = new HashMap<>();
    private final HashMap<UUID, LinkedList<Advice>> lastAdvices = new HashMap<>();
    private int nextId = 0;
    private int noRepetition = 4;

    private void register(String hero, String text, int chance) {
        Advice advice = new Advice(text, chance, nextId);
        nextId++;
        if (!advices.containsKey(hero)) {
            advices.put(hero, new ArrayList<>());
        }
        advices.get(hero).add(advice);
    }

    public void forgetPlayer(UUID uuid) {
        lastAdvices.remove(uuid);
    }

    public String nextAdvice(GamePlayer player) {
        if (!lastAdvices.containsKey(player.getPlayer().getUniqueId())) {
            lastAdvices.put(player.getPlayer().getUniqueId(), new LinkedList<>());
        }
        Advice result = null;
        List<Advice> suitable = new ArrayList<>();
        suitable.addAll(advices.get("all"));
        suitable.addAll(advices.get(player.getClassName()));
        int summaryChance = 0;
        for (Advice advice : suitable) {
            summaryChance += advice.chance;
        }
        Random random = new Random();
        for (int attempt = 0; attempt < suitable.size() * 2; ++attempt) {
            int number = random.nextInt(summaryChance);
            int currentSum = 0;
            for (Advice advice : suitable) {
                currentSum += advice.chance;
                if (currentSum >= number) {
                    boolean ignore = false;
                    for (Advice lastAdvice : lastAdvices.get(player.getPlayer().getUniqueId())) {
                        if (lastAdvice.id == advice.id) {
                            ignore = true;
                            break;
                        }
                    }
                    if (!ignore) {
                        result = advice;
                    }
                    break;
                }
            }
            if (result != null) {
                break;
            }
        }
        if (result != null) {
            if (lastAdvices.get(player.getPlayer().getUniqueId()).size() == noRepetition) {
                lastAdvices.get(player.getPlayer().getUniqueId()).removeFirst();
            }
            lastAdvices.get(player.getPlayer().getUniqueId()).add(result);
        }
        if (result == null) {
            return null;
        }
        return result.text;
    }

    public int getNoRepetition() {
        return noRepetition;
    }

    public void setNoRepetition(int noRepetition) {
        this.noRepetition = noRepetition;
    }
}
