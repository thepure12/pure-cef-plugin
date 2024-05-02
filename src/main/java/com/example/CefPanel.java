package com.example;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.friwi.jcefmaven.CefAppBuilder;
import net.runelite.client.ui.PluginPanel;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CefPanel extends PluginPanel {

    private final String DEV_BASE_URL = "http://localhost:5500"; // If using npm
    private final int REMOTE_DEBUGGING_PORT = 8888;
    private String baseUrl = "https://purecef.me";
    private static final long serialVersionUID = -5570653778104813836L;


    public static JFrame createFrame() {
        return createFrame(false, false,false,false);
    }

    public static JFrame createFrame(boolean useOSR, boolean isTransparent, boolean remoteDebugging, boolean devMode) {
        JFrame frame = new JFrame();
        JPanel panel = createPanel(useOSR, isTransparent, remoteDebugging, devMode);
        frame.add(panel);
        frame.setSize(panel.getSize());
        frame.pack();
        frame.setVisible(true);
        return frame;
    }

    public static CefPanel createPanel() {
        return createPanel(false, false, false, false);
    }

    public static CefPanel createPanel(boolean useOSR, boolean isTransparent, boolean remoteDebugging, boolean devMode) {
        return new CefPanel(useOSR, isTransparent, remoteDebugging, devMode);
    }


    @SneakyThrows
    public CefPanel(boolean useOSR, boolean isTransparent, boolean remoteDebugging, boolean devMode) {
        super();
        CefApp cefApp;
        CefAppBuilder builder = new CefAppBuilder();
        CefSettings settings = builder.getCefSettings();
        List<String> args = new ArrayList<>();

        if (remoteDebugging) {
            args.add("--disable-web-security");
            args.add("--remote-allow-origins=*");
            settings.remote_debugging_port = REMOTE_DEBUGGING_PORT;
        }
        settings.windowless_rendering_enabled = useOSR;
        settings.cache_path = ExampleAPI.RUNELITE_DIR.resolve("pure-example-jcef/cache").toString();

        if (devMode) {
            baseUrl = DEV_BASE_URL;
        }

        if (CefApp.getState() == CefApp.CefAppState.INITIALIZED) {
            cefApp = CefApp.getInstance(args.toArray(new String[0]));

        } else {
            builder.setInstallDir(ExampleAPI.RUNELITE_DIR.resolve("pure-example-jcef").toFile());
            for (String arg : args) {
                builder.addJcefArgs(arg);
            }
            cefApp = builder.build();
        }

        CefClient client = cefApp.createClient();
        // Register schemes after client is created
        SchemeHandler[] resourceHandlerAdapters = {
                new ClientSchemeHandler()
        };
        for (SchemeHandler schemeHandler : resourceHandlerAdapters) {
            log.info("Adding scheme handler for " + schemeHandler.getScheme() + "://" + schemeHandler.getDomain());
            cefApp.registerSchemeHandlerFactory(schemeHandler.getScheme(), schemeHandler.getDomain(), (cefBrowser, cefFrame, s, cefRequest) -> schemeHandler);
        }
        CefBrowser browser = client.createBrowser(baseUrl + "/index.html", useOSR, isTransparent);
        Component browserUI = browser.getUIComponent();
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(225, 500));
        setVisible(true);
        add(browserUI);
    }

    public JFrame packInFrame() {
        JFrame frame = new JFrame();
        frame.add(this);
        frame.setPreferredSize(getSize());
        frame.pack();
        frame.setVisible(true);
        return frame;
    }
}
