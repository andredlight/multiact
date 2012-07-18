package org.ruboto;

import org.ruboto.Script;
import java.io.IOException;

public class RubotoService extends android.app.Service {
  private String scriptName;
  public Object[] args;
  private Object rubyInstance;

  public static final int CB_BIND = 0;
  public static final int CB_CONFIGURATION_CHANGED = 1;
  public static final int CB_DESTROY = 2;
  public static final int CB_LOW_MEMORY = 3;
  public static final int CB_REBIND = 4;
  public static final int CB_UNBIND = 5;
  public static final int CB_START_COMMAND = 6;
  public static final int CB_TASK_REMOVED = 7;
  public static final int CB_TRIM_MEMORY = 8;

  private Object[] callbackProcs = new Object[9];

  public void setCallbackProc(int id, Object obj) {
    callbackProcs[id] = obj;
  }
	
  public void setScriptName(String name){
    scriptName = name;
  }

  /****************************************************************************************
   * 
   *  Activity Lifecycle: onCreate
   */
	
  @Override
  public void onCreate() {
	System.out.println("RubotoService.onCreate()");
    args = new Object[0];

    super.onCreate();

    if (JRubyAdapter.setUpJRuby(this)) {
        // TODO(uwe):  Only needed for non-class-based definitions
        // Can be removed if we stop supporting non-class-based definitions
    	JRubyAdapter.defineGlobalVariable("$context", this);
    	JRubyAdapter.defineGlobalVariable("$service", this);
    	// TODO end

        try {
            if (scriptName != null) {
                String rubyClassName = Script.toCamelCase(scriptName);
                System.out.println("Looking for Ruby class: " + rubyClassName);
                Object rubyClass = JRubyAdapter.get(rubyClassName);
                if (rubyClass == null) {
                    System.out.println("Loading script: " + scriptName);
                    JRubyAdapter.exec(new Script(scriptName).getContents());
                    rubyClass = JRubyAdapter.get(rubyClassName);
                }
                if (rubyClass != null) {
                    System.out.println("Instanciating Ruby class: " + rubyClassName);
                    rubyInstance = JRubyAdapter.callMethod(rubyClass, "new", this, Object.class);
                    JRubyAdapter.callMethod(rubyInstance, "on_create");
                }
            } else {
            	JRubyAdapter.execute("$service.initialize_ruboto");
            	JRubyAdapter.execute("$service.on_create");
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    } else {
      // FIXME(uwe):  What to do if the Ruboto Core plarform cannot be found?
    }
  }

  /****************************************************************************************
   * 
   *  Generated Methods
   */

  public android.os.IBinder onBind(android.content.Intent intent) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_BIND] != null) {
        return (android.os.IBinder) JRubyAdapter.callMethod(callbackProcs[CB_BIND], "call" , intent, android.os.IBinder.class);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_bind}")) {
          return (android.os.IBinder) JRubyAdapter.callMethod(rubyInstance, "on_bind" , intent, android.os.IBinder.class);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onBind}")) {
            return (android.os.IBinder) JRubyAdapter.callMethod(rubyInstance, "onBind" , intent, android.os.IBinder.class);
          } else {
            return null;
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onBind");
      return null;
    }
  }

  public void onConfigurationChanged(android.content.res.Configuration newConfig) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_CONFIGURATION_CHANGED] != null) {
        super.onConfigurationChanged(newConfig);
        JRubyAdapter.callMethod(callbackProcs[CB_CONFIGURATION_CHANGED], "call" , newConfig);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_configuration_changed}")) {
          super.onConfigurationChanged(newConfig);
          JRubyAdapter.callMethod(rubyInstance, "on_configuration_changed" , newConfig);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onConfigurationChanged}")) {
            super.onConfigurationChanged(newConfig);
            JRubyAdapter.callMethod(rubyInstance, "onConfigurationChanged" , newConfig);
          } else {
            super.onConfigurationChanged(newConfig);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onConfigurationChanged");
      super.onConfigurationChanged(newConfig);
    }
  }

  public void onDestroy() {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_DESTROY] != null) {
        super.onDestroy();
        JRubyAdapter.callMethod(callbackProcs[CB_DESTROY], "call" );
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_destroy}")) {
          super.onDestroy();
          JRubyAdapter.callMethod(rubyInstance, "on_destroy" );
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onDestroy}")) {
            super.onDestroy();
            JRubyAdapter.callMethod(rubyInstance, "onDestroy" );
          } else {
            super.onDestroy();
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onDestroy");
      super.onDestroy();
    }
  }

  public void onLowMemory() {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_LOW_MEMORY] != null) {
        super.onLowMemory();
        JRubyAdapter.callMethod(callbackProcs[CB_LOW_MEMORY], "call" );
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_low_memory}")) {
          super.onLowMemory();
          JRubyAdapter.callMethod(rubyInstance, "on_low_memory" );
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onLowMemory}")) {
            super.onLowMemory();
            JRubyAdapter.callMethod(rubyInstance, "onLowMemory" );
          } else {
            super.onLowMemory();
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onLowMemory");
      super.onLowMemory();
    }
  }

  public void onRebind(android.content.Intent intent) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_REBIND] != null) {
        super.onRebind(intent);
        JRubyAdapter.callMethod(callbackProcs[CB_REBIND], "call" , intent);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_rebind}")) {
          super.onRebind(intent);
          JRubyAdapter.callMethod(rubyInstance, "on_rebind" , intent);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onRebind}")) {
            super.onRebind(intent);
            JRubyAdapter.callMethod(rubyInstance, "onRebind" , intent);
          } else {
            super.onRebind(intent);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onRebind");
      super.onRebind(intent);
    }
  }

  public boolean onUnbind(android.content.Intent intent) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_UNBIND] != null) {
        super.onUnbind(intent);
        return (Boolean) JRubyAdapter.callMethod(callbackProcs[CB_UNBIND], "call" , intent, Boolean.class);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_unbind}")) {
          super.onUnbind(intent);
          return (Boolean) JRubyAdapter.callMethod(rubyInstance, "on_unbind" , intent, Boolean.class);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onUnbind}")) {
            super.onUnbind(intent);
            return (Boolean) JRubyAdapter.callMethod(rubyInstance, "onUnbind" , intent, Boolean.class);
          } else {
            return super.onUnbind(intent);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onUnbind");
      return super.onUnbind(intent);
    }
  }

  public int onStartCommand(android.content.Intent intent, int flags, int startId) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_START_COMMAND] != null) {
        super.onStartCommand(intent, flags, startId);
        return (Integer) JRubyAdapter.callMethod(callbackProcs[CB_START_COMMAND], "call" , new Object[]{intent, flags, startId}, Integer.class);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_start_command}")) {
          super.onStartCommand(intent, flags, startId);
          return (Integer) JRubyAdapter.callMethod(rubyInstance, "on_start_command" , new Object[]{intent, flags, startId}, Integer.class);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onStartCommand}")) {
            super.onStartCommand(intent, flags, startId);
            return (Integer) JRubyAdapter.callMethod(rubyInstance, "onStartCommand" , new Object[]{intent, flags, startId}, Integer.class);
          } else {
            return super.onStartCommand(intent, flags, startId);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onStartCommand");
      return super.onStartCommand(intent, flags, startId);
    }
  }

  public void onTaskRemoved(android.content.Intent arg0) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_TASK_REMOVED] != null) {
        super.onTaskRemoved(arg0);
        JRubyAdapter.callMethod(callbackProcs[CB_TASK_REMOVED], "call" , arg0);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_task_removed}")) {
          super.onTaskRemoved(arg0);
          JRubyAdapter.callMethod(rubyInstance, "on_task_removed" , arg0);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onTaskRemoved}")) {
            super.onTaskRemoved(arg0);
            JRubyAdapter.callMethod(rubyInstance, "onTaskRemoved" , arg0);
          } else {
            super.onTaskRemoved(arg0);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onTaskRemoved");
      super.onTaskRemoved(arg0);
    }
  }

  public void onTrimMemory(int arg0) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_TRIM_MEMORY] != null) {
        super.onTrimMemory(arg0);
        JRubyAdapter.callMethod(callbackProcs[CB_TRIM_MEMORY], "call" , arg0);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_trim_memory}")) {
          super.onTrimMemory(arg0);
          JRubyAdapter.callMethod(rubyInstance, "on_trim_memory" , arg0);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onTrimMemory}")) {
            super.onTrimMemory(arg0);
            JRubyAdapter.callMethod(rubyInstance, "onTrimMemory" , arg0);
          } else {
            super.onTrimMemory(arg0);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onTrimMemory");
      super.onTrimMemory(arg0);
    }
  }

}


