package cf.gamesukimanirs.realtimesync;

//java
import java.io.File;
import java.text.SimpleDateFormat;
/*import java.util.ArrayList;*/
import java.util.Calendar;
import java.util.LinkedHashMap;

//command
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

//level
import cn.nukkit.level.GameRule;

//config
import cn.nukkit.utils.Config;

//scheduler
import cn.nukkit.scheduler.TaskHandler;

//event
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;

//base
import cn.nukkit.Player;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.PluginBase;

public class Main extends PluginBase implements Listener{

	private String PluginName = "RealTimeSync";
	private String version = "1.0.0";
	private Config stg;
	//private Config rtdata;
	private TaskHandler rtTask;

	@SuppressWarnings({ "serial", "deprecation" })
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info(PluginName + "-v" + version + "を読み込みました。作者:gamesukimanIRS");
    	getLogger().warning("製作者偽りと二次配布、友人用を除いた他人用の改造、改造配布、プラグインの横流し、悪用等はおやめ下さい。");
    	getLogger().warning("また、このプラグインを使用して発生した如何なる問題に対しての責任は負いかねます。");
    	getLogger().info("このプラグインを使用する際はどこかにプラグイン名「" + PluginName + "」と作者名「gamesukimanIRS」を記載して頂けると光栄です。");
    	stg = new Config(new File(getDataFolder(), "settings.yml"), Config.YAML,
				new LinkedHashMap<String,Object>() {{
			put("#設定。trueもしくはfalse。","");
			put("#設定の初期化を行いたい場合、ymlごと削除を推奨します。","");
			put("#現実時間同期","");
			put("rt","false");
			put("#デバッグモード。通常時はfalse推奨。","");
			put("debugmode","false");
		}});
    	/*ArrayList<String> rta = new ArrayList<String>() {{add("world");}};
		rtdata = new Config(new File(getDataFolder(), "rtdata.yml"), Config.YAML,
				new LinkedHashMap<String,Object>() {{
			put("world",rta);
		}});*/
    	///debug
    	if(!stg.exists("debugmode")) {stg.set("debugmode", false);stg.save();}
    	Object debugv = stg.get("debugmode");
    	if(!debugv.equals(true)&&!debugv.equals(false)) {stg.set("debugmode", false);stg.save();}
    	///realtime
    	if(!stg.exists("rt")) {stg.set("rt", false);stg.save();}
    	Object rtv = stg.get("rt");
    	if(!rtv.equals(true)&&!rtv.equals(false)) {stg.set("rt", false);stg.save();}
    	if(rtv.equals(true)) {
    		realTimeTask();
    		getServer().getLevels().forEach((number,level) ->{
    			level.stopTime();
    			level.gameRules.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
    		});
    	}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.isOp()) {
			try {
				switch(cmd.getName()) {
					case "rton":
						if(stg.get("rt").equals(true)) {
							sender.sendMessage("§a[RealTimeSync]§cエラー:003 既に同期されています。");
							return true;
						}
						stg.set("rt", true);
						stg.save();
						realTimeTask();
						getServer().getLevels().forEach((number,level) ->{
							level.stopTime();
							level.gameRules.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
						});
						getLogger().notice(sender.getName()+"によって現実時間のゲーム内時間への同期が有効になりました。");
						getLogger().debug("TimeSyncTaskID:"+rtTask.getTaskId());
						if(stg.get("debugmode").equals(true)){
							getLogger().info("[DEBUG]スケジューラー時刻同期タスクID:"+rtTask.getTaskId());
						}
						sender.sendMessage("§a[RealTimeSync]§b現実時間とゲーム内時間は同期されます。");
						return true;
					case "rtoff":
						if(stg.get("rt").equals(false)) {
							sender.sendMessage("§a[RealTimeSync]§cエラー:004 同期は行われていません。");
							return true;
						}
						stg.set("rt", false);
						stg.save();
						getServer().getLevels().forEach((number,level) ->{
							level.startTime();
							level.gameRules.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
						});
						getServer().getScheduler().cancelTask(rtTask.getTaskId());
						getLogger().notice(sender.getName()+"によって現実時間のゲーム内時間への同期が無効になりました。");
						sender.sendMessage("§a[RealTimeSync]§b現実時間とゲーム内時間は同期されません。");
						return true;
				}
				return false;
			}catch(ArrayIndexOutOfBoundsException e) {
				sender.sendMessage("§a[RealTimeSync]§cエラー:002 メッセージを入力してください");
				return false;
			}
		}else {
			sender.sendMessage("§a[RealTimeSync]§cエラー：001 OP権限がありません");
			return true;
		}
	}

	@EventHandler
	public void onCmdPreprocess(PlayerCommandPreprocessEvent e) {
		if(e.getMessage().startsWith("/time")){
			Player p = e.getPlayer();
			if(e.getMessage().startsWith("/time query")) {
				p.sendMessage("現在の時刻: "+p.getLevel().getTime());
				e.setCancelled();
			}else {
				if(stg.get("rt").equals(true)) {
					p.sendMessage("§a[RealTimeSync]§cエラー：005 時間同期中は時刻設定ができません");
					p.sendMessage("使い方: /time <query>");
					e.setCancelled();
				}
			}
		}
	}

	private void realTimeTask() {
		rtTask = getServer().getScheduler().scheduleRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				timeTask();
			}
		},72);
		if(stg.get("debugmode").equals(true)) {
			getLogger().info("[DEBUG]タスクID:"+rtTask.getTaskId());
		}
	}

	public void timeTask() {
		int timeh = Integer.parseInt(new SimpleDateFormat("HH").format(Calendar.getInstance().getTime()));
		int timem = Integer.parseInt(new SimpleDateFormat("mm").format(Calendar.getInstance().getTime()));
		int times = Integer.parseInt(new SimpleDateFormat("ss").format(Calendar.getInstance().getTime()));
		double timea = timeh*3600+timem*60+times;
		double timemc = timea/3.6;
		int timemc2 = (int)Math.floor(timemc)-6000;
		getServer().getLevels().forEach((number,level) ->{
			level.setTime(timemc2);
		});
		getLogger().debug("00:00:00+"+timea+"s set time to "+timemc2);
		if(stg.get("debugmode").equals(true)) {
			getLogger().info("[DEBUG]00:00:00から"+timea+"秒経ち、時刻を"+timemc2+"にセット。");
		}
	}
}