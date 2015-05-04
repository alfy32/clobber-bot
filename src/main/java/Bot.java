import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Bot
{
    private final String name;
    private final String className;
    private final Constructor<? extends ClobberBot> constructor;

    static final Clobber CLOBBER = Clobber.create(new String[0]);

    static Bot create(Class<? extends ClobberBot> aClass)
    {
        try {
            return new Bot(aClass);
        }
        catch (Exception ex) {
            Logger.getLogger(Bots.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private Bot(Class<? extends ClobberBot> aClass) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException
    {
        constructor = aClass.getConstructor(Clobber.class);
        ClobberBot bot = constructor.newInstance(CLOBBER);
        name = bot.toString();
        className = aClass.getSimpleName();
    }

    @Override
    public String toString()
    {
        return String.format("%s (%s)", name, className);
    }

    ClobberBot newInstance(Clobber game)
    {
        try {
            return constructor.newInstance(game);
        }
        catch (Exception ex) {
            Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}