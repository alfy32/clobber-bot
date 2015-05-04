import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import edu.boisestate.cs.ai.clobberbot.utils.BotClassLoader;

public final class Bots
{
    public static final Set<Bot> BOTS = new HashSet();
    private static final BotClassLoader<ClobberBot> LOADER = new BotClassLoader(ClobberBot.class);

    private Bots()
    {
    }

    static {
        BOTS.add(Bot.create(BrutalBotMPetriOffshootTeammate.class));
        BOTS.add(Bot.create(BrutalBotMPetriOffshootTeammate.class));
        BOTS.add(Bot.create(Conqueror.class));
        BOTS.add(Bot.create(KillerInstinctBot.class));
        BOTS.add(Bot.create(GUIClobberBot.class));
        for (Class<? extends ClobberBot> bot : LOADER.loadDir(Bots.class.getResource("bots"))) {
            BOTS.add(Bot.create(bot));
        }
    }

    public static Iterable<Bot> loaded()
    {
        return BOTS;
    }

    public static List<ClobberBot> createAll(Clobber game)
    {
        List<ClobberBot> instances = new ArrayList(BOTS.size());
        for (Bot bot : BOTS) {
            ClobberBot cb = bot.newInstance(game);
            if (cb != null)
                instances.add(cb);
        }
        return instances;
    }

    static List<ClobberBot> newInstance(Clobber game, Object[] selectedValuesList)
    {
        List<ClobberBot> instances = new ArrayList();
        for (Object object : selectedValuesList) {
            if (object instanceof Bot)
                instances.add(((Bot) object).newInstance(game));
        }
        return instances;
    }

    static List<ClobberBot> randomMatchup(Clobber game, int i)
    {
        List<ClobberBot> match = new ArrayList();
        ArrayList<Bot> arrayList = new ArrayList(BOTS);
        Random random = new Random();
        for (int j = 0; j < i; j++) {
            match.add(arrayList.remove(random.nextInt(arrayList.size())).newInstance(game));
        }
        return match;
    }

    static List<Bot> load(File dir)
    {
        List<Bot> bots = new ArrayList();
        for (Class<? extends ClobberBot> botClass : LOADER.loadDir(dir)) {
            Bot bot = Bot.create(botClass);
            if (!BOTS.contains(bot))
                bots.add(bot);
            BOTS.add(bot);
        }
        return bots;
    }
}
