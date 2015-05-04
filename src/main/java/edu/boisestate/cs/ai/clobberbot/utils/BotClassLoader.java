package edu.boisestate.cs.ai.clobberbot.utils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BotClassLoader<B> extends ClassLoader
{
    final Class<B> clobberBotClass;
    private static final Logger LOG = Logger.getLogger(BotClassLoader.class.getName());

    public BotClassLoader(Class<B> aClass)
    {
        clobberBotClass = aClass;
    }

    public Collection<Class<? extends B>> loadDir(URL url)
    {
        try {
            if ("jar".equals(url.getProtocol()))
                return loadBotsFromJar(url);
            else
                return loadDir(new File(url.toURI()));
        }
        catch (URISyntaxException ex) {
            //Ignore location if url is bad
        }
        return new ArrayList(0);
    }

    public Collection<Class<? extends B>> loadDir(File dir)
    {
        if (dir.isFile())
        {
            Class<? extends B> bot = loadBot(dir);
            ArrayList bots = new ArrayList(1);
            bots.add(bot);
            return bots;
        }
        return loadBots(dir, dir.listFiles(new FileFilter()
        {
            @Override
            public boolean accept(File pathname)
            {
                return pathname.getName().endsWith(".class");
            }
        }));
    }

    public Class<? extends B> loadBot(File file)
    {
        try {
            File dir = file.getParentFile();
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{dir.toURI().toURL()}, clobberBotClass.getClassLoader());
            String name = file.getName().substring(0, file.getName().length() - 6);
            Class<?> loadClass = urlClassLoader.loadClass(name);
            return loadClass.asSubclass(clobberBotClass);
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private Collection<Class<? extends B>> loadBotsFromJar(URL url)
    {
        List<Class<? extends B>> bots = new ArrayList();
        String[] split = url.getPath().split("!/?");
        try {
            JarURLConnection jc = (JarURLConnection) url.openConnection();
            JarFile jarFile = jc.getJarFile();
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String entryName = jarEntry.getName();
                if (entryName.startsWith(split[1]) && entryName.endsWith(".class")) {
                    int slash = entryName.lastIndexOf('/') + 1;
                    String name = entryName.substring(slash, entryName.length() - 6);
                    DataInputStream inputStream = null;
                    try {
                        byte[] data = new byte[(int) jarEntry.getSize()];
                        inputStream = new DataInputStream(jarFile.getInputStream(jarEntry));
                        inputStream.readFully(data);
                        Class<?> loadedClass = defineClass(name, data, 0, data.length);
                        bots.add(loadedClass.asSubclass(clobberBotClass));
                    }
                    catch (Exception e) {
                        LOG.log(Level.SEVERE, null, e);
                    } finally {
                        if (inputStream != null)
                            inputStream.close();
                    }
                }
            }
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return bots;
    }

    private Collection<Class<? extends B>> loadBots(File dir, File[] files)
    {
        List<Class<? extends B>> bots = new ArrayList();
        try {
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{dir.toURI().toURL()}, clobberBotClass.getClassLoader());
			for (File file : files) {
				try {
						String name = file.getName().substring(0, file.getName().length() - 6);
						Class<?> loadClass = urlClassLoader.loadClass(name);
						bots.add(loadClass.asSubclass(clobberBotClass));
				}
				catch (Exception e) {
					LOG.log(Level.SEVERE, null, e);
				}
			}
        }
        catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return bots;
    }
}
