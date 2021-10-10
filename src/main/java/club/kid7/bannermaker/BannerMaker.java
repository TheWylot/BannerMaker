package club.kid7.bannermaker;

import club.kid7.bannermaker.command.BannerMakerCommand;
import club.kid7.bannermaker.configuration.DefaultConfig;
import club.kid7.bannermaker.configuration.Language;
import club.kid7.pluginutilities.command.CommandComponent;
import club.kid7.pluginutilities.configuration.KConfigManager;
import club.kid7.pluginutilities.gui.CustomGUI;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class BannerMaker extends JavaPlugin {
    private static BannerMaker instance = null;
    public Economy econ = null;
    public boolean enableAlphabetAndNumber = true;
    public PlayerDataMap playerDataMap = null;

    @Override
    public void onEnable() {
        instance = this;

        //Commands
        registerCommands();

        //CustomGUI
        CustomGUI.enable();
        //Config
        List<String> configList = Arrays.asList("config", "price");
        for (String config : configList) {
            KConfigManager.load(config);
        }
        //Reload
        reload();
        //bStats
        int pluginId = 383;
        new Metrics(this, pluginId);
    }

    @Override
    public void onDisable() {
        //CustomGUI
        CustomGUI.disable();
    }

    public static BannerMaker getInstance() {
        return instance;
    }

    public void reload() {
        //Reload Config
        KConfigManager.reloadAll();
        //載入語言包
        new Language(this).loadLanguage();
        //Check Default Config
        new DefaultConfig(this).checkConfig();
        //經濟
        if (setupEconomy()) {
            getLogger().info("Vault dependency found! Enable economy supported");
        } else {
            getLogger().info("Disable economy supported");
        }
        //設定檔
        FileConfiguration config = KConfigManager.get("config");
        if (config != null) {
            //字母與數字
            enableAlphabetAndNumber = config.getBoolean("AlphabetAndNumberBanner.Enable", true);
        }
        //玩家資料
        playerDataMap = new PlayerDataMap();
    }

    private boolean setupEconomy() {
        econ = null;
        //檢查設定
        FileConfiguration config = KConfigManager.get("config");
        //若無啟用經濟
        if (!config.getBoolean("Economy.Enable", false)) {
            return false;
        }

        //檢查Vault
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        //檢查經濟支援
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private void registerCommands() {
        CommandComponent bmCommand = new BannerMakerCommand(this);
        getCommand("BannerMaker").setExecutor(bmCommand);
        getCommand("BannerMaker").setTabCompleter(bmCommand);
    }
}
