package org.ruboto;

import java.io.IOException;

import org.ruboto.Script;

import android.app.ProgressDialog;
import android.os.Bundle;

public class RubotoActivity extends android.app.Activity {
    private String scriptName;
    private String remoteVariable = null;
    private Object[] args;
    private Bundle configBundle = null;
    private Object rubyInstance;

  public static final int CB_ACTIVITY_RESULT = 0;
  public static final int CB_CHILD_TITLE_CHANGED = 1;
  public static final int CB_CONFIGURATION_CHANGED = 2;
  public static final int CB_CONTENT_CHANGED = 3;
  public static final int CB_CONTEXT_ITEM_SELECTED = 4;
  public static final int CB_CONTEXT_MENU_CLOSED = 5;
  public static final int CB_CREATE_CONTEXT_MENU = 6;
  public static final int CB_CREATE_DESCRIPTION = 7;
  public static final int CB_CREATE_OPTIONS_MENU = 8;
  public static final int CB_CREATE_PANEL_MENU = 9;
  public static final int CB_CREATE_PANEL_VIEW = 10;
  public static final int CB_CREATE_THUMBNAIL = 11;
  public static final int CB_CREATE_VIEW = 12;
  public static final int CB_DESTROY = 13;
  public static final int CB_KEY_DOWN = 14;
  public static final int CB_KEY_MULTIPLE = 15;
  public static final int CB_KEY_UP = 16;
  public static final int CB_LOW_MEMORY = 17;
  public static final int CB_MENU_ITEM_SELECTED = 18;
  public static final int CB_MENU_OPENED = 19;
  public static final int CB_NEW_INTENT = 20;
  public static final int CB_OPTIONS_ITEM_SELECTED = 21;
  public static final int CB_OPTIONS_MENU_CLOSED = 22;
  public static final int CB_PANEL_CLOSED = 23;
  public static final int CB_PAUSE = 24;
  public static final int CB_POST_CREATE = 25;
  public static final int CB_POST_RESUME = 26;
  public static final int CB_PREPARE_OPTIONS_MENU = 27;
  public static final int CB_PREPARE_PANEL = 28;
  public static final int CB_RESTART = 29;
  public static final int CB_RESTORE_INSTANCE_STATE = 30;
  public static final int CB_RESUME = 31;
  public static final int CB_SAVE_INSTANCE_STATE = 32;
  public static final int CB_SEARCH_REQUESTED = 33;
  public static final int CB_START = 34;
  public static final int CB_STOP = 35;
  public static final int CB_TITLE_CHANGED = 36;
  public static final int CB_TOUCH_EVENT = 37;
  public static final int CB_TRACKBALL_EVENT = 38;
  public static final int CB_WINDOW_ATTRIBUTES_CHANGED = 39;
  public static final int CB_WINDOW_FOCUS_CHANGED = 40;
  public static final int CB_USER_INTERACTION = 41;
  public static final int CB_USER_LEAVE_HINT = 42;
  public static final int CB_ATTACHED_TO_WINDOW = 43;
  public static final int CB_BACK_PRESSED = 44;
  public static final int CB_DETACHED_FROM_WINDOW = 45;
  public static final int CB_KEY_LONG_PRESS = 46;
  public static final int CB_ACTION_MODE_FINISHED = 47;
  public static final int CB_ACTION_MODE_STARTED = 48;
  public static final int CB_ATTACH_FRAGMENT = 49;
  public static final int CB_KEY_SHORTCUT = 50;
  public static final int CB_WINDOW_STARTING_ACTION_MODE = 51;
  public static final int CB_GENERIC_MOTION_EVENT = 52;
  public static final int CB_TRIM_MEMORY = 53;
  public static final int CB_APPLY_THEME_RESOURCE = 54;

    private Object[] callbackProcs = new Object[56];

    public void setCallbackProc(int id, Object obj) {
        callbackProcs[id] = obj;
    }
	
    public RubotoActivity setRemoteVariable(String var) {
        remoteVariable = var;
        return this;
    }

    public String getRemoteVariableCall(String call) {
        return (remoteVariable == null ? "" : (remoteVariable + ".")) + call;
    }

    public void setScriptName(String name) {
        scriptName = name;
    }

    /****************************************************************************************
     *
     *  Activity Lifecycle: onCreate
     */
	
    @Override
    public void onCreate(Bundle bundle) {
        args = new Object[1];
        args[0] = bundle;

        configBundle = getIntent().getBundleExtra("RubotoActivity Config");

        if (configBundle != null) {
            if (configBundle.containsKey("Theme")) {
                setTheme(configBundle.getInt("Theme"));
            }
            if (configBundle.containsKey("Script")) {
                if (this.getClass().getName() == RubotoActivity.class.getName()) {
                    setScriptName(configBundle.getString("Script"));
                } else {
                    throw new IllegalArgumentException("Only local Intents may set script name.");
                }
            }
        }

        super.onCreate(bundle);
    
        if (JRubyAdapter.isInitialized()) {
            prepareJRuby();
    	    loadScript();
        }
    }

    // TODO(uwe):  Only needed for non-class-based definitions
    // Can be removed if we stop supporting non-class-based definitions
    // This causes JRuby to initialize and takes a while.
    protected void prepareJRuby() {
    	JRubyAdapter.put("$context", this);
    	JRubyAdapter.put("$activity", this);
    	JRubyAdapter.put("$bundle", args[0]);
    }
    // TODO end

    protected void loadScript() {
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
                    JRubyAdapter.callMethod(rubyInstance, "on_create", args[0]);
                }
            } else if (configBundle != null) {
                // TODO: Why doesn't this work?
                // JRubyAdapter.callMethod(this, "initialize_ruboto");
            	JRubyAdapter.execute("$activity.initialize_ruboto");
                // TODO: Why doesn't this work?
                // JRubyAdapter.callMethod(this, "on_create", args[0]);
            	JRubyAdapter.execute("$activity.on_create($bundle)");
            }
        } catch(IOException e){
            e.printStackTrace();
            ProgressDialog.show(this, "Script failed", "Something bad happened", true, true);
        }
    }

    public boolean rubotoAttachable() {
      return true;
    }

  /****************************************************************************************
   * 
   *  Generated Methods
   */

  public void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_ACTIVITY_RESULT] != null) {
        super.onActivityResult(requestCode, resultCode, data);
        JRubyAdapter.callMethod(callbackProcs[CB_ACTIVITY_RESULT], "call" , new Object[]{requestCode, resultCode, data});
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_activity_result}")) {
          super.onActivityResult(requestCode, resultCode, data);
          JRubyAdapter.callMethod(rubyInstance, "on_activity_result" , new Object[]{requestCode, resultCode, data});
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onActivityResult}")) {
            super.onActivityResult(requestCode, resultCode, data);
            JRubyAdapter.callMethod(rubyInstance, "onActivityResult" , new Object[]{requestCode, resultCode, data});
          } else {
            super.onActivityResult(requestCode, resultCode, data);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onActivityResult");
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

  public void onChildTitleChanged(android.app.Activity childActivity, java.lang.CharSequence title) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_CHILD_TITLE_CHANGED] != null) {
        super.onChildTitleChanged(childActivity, title);
        JRubyAdapter.callMethod(callbackProcs[CB_CHILD_TITLE_CHANGED], "call" , new Object[]{childActivity, title});
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_child_title_changed}")) {
          super.onChildTitleChanged(childActivity, title);
          JRubyAdapter.callMethod(rubyInstance, "on_child_title_changed" , new Object[]{childActivity, title});
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onChildTitleChanged}")) {
            super.onChildTitleChanged(childActivity, title);
            JRubyAdapter.callMethod(rubyInstance, "onChildTitleChanged" , new Object[]{childActivity, title});
          } else {
            super.onChildTitleChanged(childActivity, title);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onChildTitleChanged");
      super.onChildTitleChanged(childActivity, title);
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

  public void onContentChanged() {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_CONTENT_CHANGED] != null) {
        super.onContentChanged();
        JRubyAdapter.callMethod(callbackProcs[CB_CONTENT_CHANGED], "call" );
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_content_changed}")) {
          super.onContentChanged();
          JRubyAdapter.callMethod(rubyInstance, "on_content_changed" );
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onContentChanged}")) {
            super.onContentChanged();
            JRubyAdapter.callMethod(rubyInstance, "onContentChanged" );
          } else {
            super.onContentChanged();
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onContentChanged");
      super.onContentChanged();
    }
  }

  public boolean onContextItemSelected(android.view.MenuItem item) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_CONTEXT_ITEM_SELECTED] != null) {
        super.onContextItemSelected(item);
        return (Boolean) JRubyAdapter.callMethod(callbackProcs[CB_CONTEXT_ITEM_SELECTED], "call" , item, Boolean.class);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_context_item_selected}")) {
          super.onContextItemSelected(item);
          return (Boolean) JRubyAdapter.callMethod(rubyInstance, "on_context_item_selected" , item, Boolean.class);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onContextItemSelected}")) {
            super.onContextItemSelected(item);
            return (Boolean) JRubyAdapter.callMethod(rubyInstance, "onContextItemSelected" , item, Boolean.class);
          } else {
            return super.onContextItemSelected(item);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onContextItemSelected");
      return super.onContextItemSelected(item);
    }
  }

  public void onContextMenuClosed(android.view.Menu menu) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_CONTEXT_MENU_CLOSED] != null) {
        super.onContextMenuClosed(menu);
        JRubyAdapter.callMethod(callbackProcs[CB_CONTEXT_MENU_CLOSED], "call" , menu);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_context_menu_closed}")) {
          super.onContextMenuClosed(menu);
          JRubyAdapter.callMethod(rubyInstance, "on_context_menu_closed" , menu);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onContextMenuClosed}")) {
            super.onContextMenuClosed(menu);
            JRubyAdapter.callMethod(rubyInstance, "onContextMenuClosed" , menu);
          } else {
            super.onContextMenuClosed(menu);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onContextMenuClosed");
      super.onContextMenuClosed(menu);
    }
  }

  public void onCreateContextMenu(android.view.ContextMenu menu, android.view.View v, android.view.ContextMenu.ContextMenuInfo menuInfo) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_CREATE_CONTEXT_MENU] != null) {
        super.onCreateContextMenu(menu, v, menuInfo);
        JRubyAdapter.callMethod(callbackProcs[CB_CREATE_CONTEXT_MENU], "call" , new Object[]{menu, v, menuInfo});
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_create_context_menu}")) {
          super.onCreateContextMenu(menu, v, menuInfo);
          JRubyAdapter.callMethod(rubyInstance, "on_create_context_menu" , new Object[]{menu, v, menuInfo});
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onCreateContextMenu}")) {
            super.onCreateContextMenu(menu, v, menuInfo);
            JRubyAdapter.callMethod(rubyInstance, "onCreateContextMenu" , new Object[]{menu, v, menuInfo});
          } else {
            super.onCreateContextMenu(menu, v, menuInfo);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onCreateContextMenu");
      super.onCreateContextMenu(menu, v, menuInfo);
    }
  }

  public java.lang.CharSequence onCreateDescription() {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_CREATE_DESCRIPTION] != null) {
        super.onCreateDescription();
        return (java.lang.CharSequence) JRubyAdapter.callMethod(callbackProcs[CB_CREATE_DESCRIPTION], "call" , java.lang.CharSequence.class);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_create_description}")) {
          super.onCreateDescription();
          return (java.lang.CharSequence) JRubyAdapter.callMethod(rubyInstance, "on_create_description" , java.lang.CharSequence.class);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onCreateDescription}")) {
            super.onCreateDescription();
            return (java.lang.CharSequence) JRubyAdapter.callMethod(rubyInstance, "onCreateDescription" , java.lang.CharSequence.class);
          } else {
            return super.onCreateDescription();
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onCreateDescription");
      return super.onCreateDescription();
    }
  }

  public boolean onCreateOptionsMenu(android.view.Menu menu) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_CREATE_OPTIONS_MENU] != null) {
        super.onCreateOptionsMenu(menu);
        return (Boolean) JRubyAdapter.callMethod(callbackProcs[CB_CREATE_OPTIONS_MENU], "call" , menu, Boolean.class);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_create_options_menu}")) {
          super.onCreateOptionsMenu(menu);
          return (Boolean) JRubyAdapter.callMethod(rubyInstance, "on_create_options_menu" , menu, Boolean.class);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onCreateOptionsMenu}")) {
            super.onCreateOptionsMenu(menu);
            return (Boolean) JRubyAdapter.callMethod(rubyInstance, "onCreateOptionsMenu" , menu, Boolean.class);
          } else {
            return super.onCreateOptionsMenu(menu);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onCreateOptionsMenu");
      return super.onCreateOptionsMenu(menu);
    }
  }

  public boolean onCreatePanelMenu(int featureId, android.view.Menu menu) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_CREATE_PANEL_MENU] != null) {
        super.onCreatePanelMenu(featureId, menu);
        return (Boolean) JRubyAdapter.callMethod(callbackProcs[CB_CREATE_PANEL_MENU], "call" , new Object[]{featureId, menu}, Boolean.class);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_create_panel_menu}")) {
          super.onCreatePanelMenu(featureId, menu);
          return (Boolean) JRubyAdapter.callMethod(rubyInstance, "on_create_panel_menu" , new Object[]{featureId, menu}, Boolean.class);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onCreatePanelMenu}")) {
            super.onCreatePanelMenu(featureId, menu);
            return (Boolean) JRubyAdapter.callMethod(rubyInstance, "onCreatePanelMenu" , new Object[]{featureId, menu}, Boolean.class);
          } else {
            return super.onCreatePanelMenu(featureId, menu);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onCreatePanelMenu");
      return super.onCreatePanelMenu(featureId, menu);
    }
  }

  public android.view.View onCreatePanelView(int featureId) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_CREATE_PANEL_VIEW] != null) {
        super.onCreatePanelView(featureId);
        return (android.view.View) JRubyAdapter.callMethod(callbackProcs[CB_CREATE_PANEL_VIEW], "call" , featureId, android.view.View.class);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_create_panel_view}")) {
          super.onCreatePanelView(featureId);
          return (android.view.View) JRubyAdapter.callMethod(rubyInstance, "on_create_panel_view" , featureId, android.view.View.class);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onCreatePanelView}")) {
            super.onCreatePanelView(featureId);
            return (android.view.View) JRubyAdapter.callMethod(rubyInstance, "onCreatePanelView" , featureId, android.view.View.class);
          } else {
            return super.onCreatePanelView(featureId);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onCreatePanelView");
      return super.onCreatePanelView(featureId);
    }
  }

  public boolean onCreateThumbnail(android.graphics.Bitmap outBitmap, android.graphics.Canvas canvas) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_CREATE_THUMBNAIL] != null) {
        super.onCreateThumbnail(outBitmap, canvas);
        return (Boolean) JRubyAdapter.callMethod(callbackProcs[CB_CREATE_THUMBNAIL], "call" , new Object[]{outBitmap, canvas}, Boolean.class);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_create_thumbnail}")) {
          super.onCreateThumbnail(outBitmap, canvas);
          return (Boolean) JRubyAdapter.callMethod(rubyInstance, "on_create_thumbnail" , new Object[]{outBitmap, canvas}, Boolean.class);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onCreateThumbnail}")) {
            super.onCreateThumbnail(outBitmap, canvas);
            return (Boolean) JRubyAdapter.callMethod(rubyInstance, "onCreateThumbnail" , new Object[]{outBitmap, canvas}, Boolean.class);
          } else {
            return super.onCreateThumbnail(outBitmap, canvas);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onCreateThumbnail");
      return super.onCreateThumbnail(outBitmap, canvas);
    }
  }

  public android.view.View onCreateView(java.lang.String name, android.content.Context context, android.util.AttributeSet attrs) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_CREATE_VIEW] != null) {
        super.onCreateView(name, context, attrs);
        return (android.view.View) JRubyAdapter.callMethod(callbackProcs[CB_CREATE_VIEW], "call" , new Object[]{name, context, attrs}, android.view.View.class);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_create_view}")) {
          super.onCreateView(name, context, attrs);
          return (android.view.View) JRubyAdapter.callMethod(rubyInstance, "on_create_view" , new Object[]{name, context, attrs}, android.view.View.class);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onCreateView}")) {
            super.onCreateView(name, context, attrs);
            return (android.view.View) JRubyAdapter.callMethod(rubyInstance, "onCreateView" , new Object[]{name, context, attrs}, android.view.View.class);
          } else {
            return super.onCreateView(name, context, attrs);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onCreateView");
      return super.onCreateView(name, context, attrs);
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

  public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_KEY_DOWN] != null) {
        super.onKeyDown(keyCode, event);
        return (Boolean) JRubyAdapter.callMethod(callbackProcs[CB_KEY_DOWN], "call" , new Object[]{keyCode, event}, Boolean.class);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_key_down}")) {
          super.onKeyDown(keyCode, event);
          return (Boolean) JRubyAdapter.callMethod(rubyInstance, "on_key_down" , new Object[]{keyCode, event}, Boolean.class);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onKeyDown}")) {
            super.onKeyDown(keyCode, event);
            return (Boolean) JRubyAdapter.callMethod(rubyInstance, "onKeyDown" , new Object[]{keyCode, event}, Boolean.class);
          } else {
            return super.onKeyDown(keyCode, event);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onKeyDown");
      return super.onKeyDown(keyCode, event);
    }
  }

  public boolean onKeyMultiple(int keyCode, int repeatCount, android.view.KeyEvent event) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_KEY_MULTIPLE] != null) {
        super.onKeyMultiple(keyCode, repeatCount, event);
        return (Boolean) JRubyAdapter.callMethod(callbackProcs[CB_KEY_MULTIPLE], "call" , new Object[]{keyCode, repeatCount, event}, Boolean.class);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_key_multiple}")) {
          super.onKeyMultiple(keyCode, repeatCount, event);
          return (Boolean) JRubyAdapter.callMethod(rubyInstance, "on_key_multiple" , new Object[]{keyCode, repeatCount, event}, Boolean.class);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onKeyMultiple}")) {
            super.onKeyMultiple(keyCode, repeatCount, event);
            return (Boolean) JRubyAdapter.callMethod(rubyInstance, "onKeyMultiple" , new Object[]{keyCode, repeatCount, event}, Boolean.class);
          } else {
            return super.onKeyMultiple(keyCode, repeatCount, event);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onKeyMultiple");
      return super.onKeyMultiple(keyCode, repeatCount, event);
    }
  }

  public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_KEY_UP] != null) {
        super.onKeyUp(keyCode, event);
        return (Boolean) JRubyAdapter.callMethod(callbackProcs[CB_KEY_UP], "call" , new Object[]{keyCode, event}, Boolean.class);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_key_up}")) {
          super.onKeyUp(keyCode, event);
          return (Boolean) JRubyAdapter.callMethod(rubyInstance, "on_key_up" , new Object[]{keyCode, event}, Boolean.class);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onKeyUp}")) {
            super.onKeyUp(keyCode, event);
            return (Boolean) JRubyAdapter.callMethod(rubyInstance, "onKeyUp" , new Object[]{keyCode, event}, Boolean.class);
          } else {
            return super.onKeyUp(keyCode, event);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onKeyUp");
      return super.onKeyUp(keyCode, event);
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

  public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_MENU_ITEM_SELECTED] != null) {
        super.onMenuItemSelected(featureId, item);
        return (Boolean) JRubyAdapter.callMethod(callbackProcs[CB_MENU_ITEM_SELECTED], "call" , new Object[]{featureId, item}, Boolean.class);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_menu_item_selected}")) {
          super.onMenuItemSelected(featureId, item);
          return (Boolean) JRubyAdapter.callMethod(rubyInstance, "on_menu_item_selected" , new Object[]{featureId, item}, Boolean.class);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onMenuItemSelected}")) {
            super.onMenuItemSelected(featureId, item);
            return (Boolean) JRubyAdapter.callMethod(rubyInstance, "onMenuItemSelected" , new Object[]{featureId, item}, Boolean.class);
          } else {
            return super.onMenuItemSelected(featureId, item);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onMenuItemSelected");
      return super.onMenuItemSelected(featureId, item);
    }
  }

  public boolean onMenuOpened(int featureId, android.view.Menu menu) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_MENU_OPENED] != null) {
        super.onMenuOpened(featureId, menu);
        return (Boolean) JRubyAdapter.callMethod(callbackProcs[CB_MENU_OPENED], "call" , new Object[]{featureId, menu}, Boolean.class);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_menu_opened}")) {
          super.onMenuOpened(featureId, menu);
          return (Boolean) JRubyAdapter.callMethod(rubyInstance, "on_menu_opened" , new Object[]{featureId, menu}, Boolean.class);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onMenuOpened}")) {
            super.onMenuOpened(featureId, menu);
            return (Boolean) JRubyAdapter.callMethod(rubyInstance, "onMenuOpened" , new Object[]{featureId, menu}, Boolean.class);
          } else {
            return super.onMenuOpened(featureId, menu);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onMenuOpened");
      return super.onMenuOpened(featureId, menu);
    }
  }

  public void onNewIntent(android.content.Intent intent) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_NEW_INTENT] != null) {
        super.onNewIntent(intent);
        JRubyAdapter.callMethod(callbackProcs[CB_NEW_INTENT], "call" , intent);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_new_intent}")) {
          super.onNewIntent(intent);
          JRubyAdapter.callMethod(rubyInstance, "on_new_intent" , intent);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onNewIntent}")) {
            super.onNewIntent(intent);
            JRubyAdapter.callMethod(rubyInstance, "onNewIntent" , intent);
          } else {
            super.onNewIntent(intent);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onNewIntent");
      super.onNewIntent(intent);
    }
  }

  public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_OPTIONS_ITEM_SELECTED] != null) {
        super.onOptionsItemSelected(item);
        return (Boolean) JRubyAdapter.callMethod(callbackProcs[CB_OPTIONS_ITEM_SELECTED], "call" , item, Boolean.class);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_options_item_selected}")) {
          super.onOptionsItemSelected(item);
          return (Boolean) JRubyAdapter.callMethod(rubyInstance, "on_options_item_selected" , item, Boolean.class);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onOptionsItemSelected}")) {
            super.onOptionsItemSelected(item);
            return (Boolean) JRubyAdapter.callMethod(rubyInstance, "onOptionsItemSelected" , item, Boolean.class);
          } else {
            return super.onOptionsItemSelected(item);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onOptionsItemSelected");
      return super.onOptionsItemSelected(item);
    }
  }

  public void onOptionsMenuClosed(android.view.Menu menu) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_OPTIONS_MENU_CLOSED] != null) {
        super.onOptionsMenuClosed(menu);
        JRubyAdapter.callMethod(callbackProcs[CB_OPTIONS_MENU_CLOSED], "call" , menu);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_options_menu_closed}")) {
          super.onOptionsMenuClosed(menu);
          JRubyAdapter.callMethod(rubyInstance, "on_options_menu_closed" , menu);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onOptionsMenuClosed}")) {
            super.onOptionsMenuClosed(menu);
            JRubyAdapter.callMethod(rubyInstance, "onOptionsMenuClosed" , menu);
          } else {
            super.onOptionsMenuClosed(menu);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onOptionsMenuClosed");
      super.onOptionsMenuClosed(menu);
    }
  }

  public void onPanelClosed(int featureId, android.view.Menu menu) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_PANEL_CLOSED] != null) {
        super.onPanelClosed(featureId, menu);
        JRubyAdapter.callMethod(callbackProcs[CB_PANEL_CLOSED], "call" , new Object[]{featureId, menu});
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_panel_closed}")) {
          super.onPanelClosed(featureId, menu);
          JRubyAdapter.callMethod(rubyInstance, "on_panel_closed" , new Object[]{featureId, menu});
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onPanelClosed}")) {
            super.onPanelClosed(featureId, menu);
            JRubyAdapter.callMethod(rubyInstance, "onPanelClosed" , new Object[]{featureId, menu});
          } else {
            super.onPanelClosed(featureId, menu);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onPanelClosed");
      super.onPanelClosed(featureId, menu);
    }
  }

  public void onPause() {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_PAUSE] != null) {
        super.onPause();
        JRubyAdapter.callMethod(callbackProcs[CB_PAUSE], "call" );
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_pause}")) {
          super.onPause();
          JRubyAdapter.callMethod(rubyInstance, "on_pause" );
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onPause}")) {
            super.onPause();
            JRubyAdapter.callMethod(rubyInstance, "onPause" );
          } else {
            super.onPause();
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onPause");
      super.onPause();
    }
  }

  public void onPostCreate(android.os.Bundle savedInstanceState) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_POST_CREATE] != null) {
        super.onPostCreate(savedInstanceState);
        JRubyAdapter.callMethod(callbackProcs[CB_POST_CREATE], "call" , savedInstanceState);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_post_create}")) {
          super.onPostCreate(savedInstanceState);
          JRubyAdapter.callMethod(rubyInstance, "on_post_create" , savedInstanceState);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onPostCreate}")) {
            super.onPostCreate(savedInstanceState);
            JRubyAdapter.callMethod(rubyInstance, "onPostCreate" , savedInstanceState);
          } else {
            super.onPostCreate(savedInstanceState);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onPostCreate");
      super.onPostCreate(savedInstanceState);
    }
  }

  public void onPostResume() {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_POST_RESUME] != null) {
        super.onPostResume();
        JRubyAdapter.callMethod(callbackProcs[CB_POST_RESUME], "call" );
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_post_resume}")) {
          super.onPostResume();
          JRubyAdapter.callMethod(rubyInstance, "on_post_resume" );
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onPostResume}")) {
            super.onPostResume();
            JRubyAdapter.callMethod(rubyInstance, "onPostResume" );
          } else {
            super.onPostResume();
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onPostResume");
      super.onPostResume();
    }
  }

  public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_PREPARE_OPTIONS_MENU] != null) {
        super.onPrepareOptionsMenu(menu);
        return (Boolean) JRubyAdapter.callMethod(callbackProcs[CB_PREPARE_OPTIONS_MENU], "call" , menu, Boolean.class);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_prepare_options_menu}")) {
          super.onPrepareOptionsMenu(menu);
          return (Boolean) JRubyAdapter.callMethod(rubyInstance, "on_prepare_options_menu" , menu, Boolean.class);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onPrepareOptionsMenu}")) {
            super.onPrepareOptionsMenu(menu);
            return (Boolean) JRubyAdapter.callMethod(rubyInstance, "onPrepareOptionsMenu" , menu, Boolean.class);
          } else {
            return super.onPrepareOptionsMenu(menu);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onPrepareOptionsMenu");
      return super.onPrepareOptionsMenu(menu);
    }
  }

  public boolean onPreparePanel(int featureId, android.view.View view, android.view.Menu menu) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_PREPARE_PANEL] != null) {
        super.onPreparePanel(featureId, view, menu);
        return (Boolean) JRubyAdapter.callMethod(callbackProcs[CB_PREPARE_PANEL], "call" , new Object[]{featureId, view, menu}, Boolean.class);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_prepare_panel}")) {
          super.onPreparePanel(featureId, view, menu);
          return (Boolean) JRubyAdapter.callMethod(rubyInstance, "on_prepare_panel" , new Object[]{featureId, view, menu}, Boolean.class);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onPreparePanel}")) {
            super.onPreparePanel(featureId, view, menu);
            return (Boolean) JRubyAdapter.callMethod(rubyInstance, "onPreparePanel" , new Object[]{featureId, view, menu}, Boolean.class);
          } else {
            return super.onPreparePanel(featureId, view, menu);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onPreparePanel");
      return super.onPreparePanel(featureId, view, menu);
    }
  }

  public void onRestart() {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_RESTART] != null) {
        super.onRestart();
        JRubyAdapter.callMethod(callbackProcs[CB_RESTART], "call" );
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_restart}")) {
          super.onRestart();
          JRubyAdapter.callMethod(rubyInstance, "on_restart" );
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onRestart}")) {
            super.onRestart();
            JRubyAdapter.callMethod(rubyInstance, "onRestart" );
          } else {
            super.onRestart();
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onRestart");
      super.onRestart();
    }
  }

  public void onRestoreInstanceState(android.os.Bundle savedInstanceState) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_RESTORE_INSTANCE_STATE] != null) {
        super.onRestoreInstanceState(savedInstanceState);
        JRubyAdapter.callMethod(callbackProcs[CB_RESTORE_INSTANCE_STATE], "call" , savedInstanceState);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_restore_instance_state}")) {
          super.onRestoreInstanceState(savedInstanceState);
          JRubyAdapter.callMethod(rubyInstance, "on_restore_instance_state" , savedInstanceState);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onRestoreInstanceState}")) {
            super.onRestoreInstanceState(savedInstanceState);
            JRubyAdapter.callMethod(rubyInstance, "onRestoreInstanceState" , savedInstanceState);
          } else {
            super.onRestoreInstanceState(savedInstanceState);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onRestoreInstanceState");
      super.onRestoreInstanceState(savedInstanceState);
    }
  }

  public void onResume() {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_RESUME] != null) {
        super.onResume();
        JRubyAdapter.callMethod(callbackProcs[CB_RESUME], "call" );
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_resume}")) {
          super.onResume();
          JRubyAdapter.callMethod(rubyInstance, "on_resume" );
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onResume}")) {
            super.onResume();
            JRubyAdapter.callMethod(rubyInstance, "onResume" );
          } else {
            super.onResume();
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onResume");
      super.onResume();
    }
  }

  public void onSaveInstanceState(android.os.Bundle outState) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_SAVE_INSTANCE_STATE] != null) {
        super.onSaveInstanceState(outState);
        JRubyAdapter.callMethod(callbackProcs[CB_SAVE_INSTANCE_STATE], "call" , outState);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_save_instance_state}")) {
          super.onSaveInstanceState(outState);
          JRubyAdapter.callMethod(rubyInstance, "on_save_instance_state" , outState);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onSaveInstanceState}")) {
            super.onSaveInstanceState(outState);
            JRubyAdapter.callMethod(rubyInstance, "onSaveInstanceState" , outState);
          } else {
            super.onSaveInstanceState(outState);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onSaveInstanceState");
      super.onSaveInstanceState(outState);
    }
  }

  public boolean onSearchRequested() {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_SEARCH_REQUESTED] != null) {
        super.onSearchRequested();
        return (Boolean) JRubyAdapter.callMethod(callbackProcs[CB_SEARCH_REQUESTED], "call" , Boolean.class);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_search_requested}")) {
          super.onSearchRequested();
          return (Boolean) JRubyAdapter.callMethod(rubyInstance, "on_search_requested" , Boolean.class);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onSearchRequested}")) {
            super.onSearchRequested();
            return (Boolean) JRubyAdapter.callMethod(rubyInstance, "onSearchRequested" , Boolean.class);
          } else {
            return super.onSearchRequested();
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onSearchRequested");
      return super.onSearchRequested();
    }
  }

  public void onStart() {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_START] != null) {
        super.onStart();
        JRubyAdapter.callMethod(callbackProcs[CB_START], "call" );
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_start}")) {
          super.onStart();
          JRubyAdapter.callMethod(rubyInstance, "on_start" );
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onStart}")) {
            super.onStart();
            JRubyAdapter.callMethod(rubyInstance, "onStart" );
          } else {
            super.onStart();
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onStart");
      super.onStart();
    }
  }

  public void onStop() {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_STOP] != null) {
        super.onStop();
        JRubyAdapter.callMethod(callbackProcs[CB_STOP], "call" );
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_stop}")) {
          super.onStop();
          JRubyAdapter.callMethod(rubyInstance, "on_stop" );
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onStop}")) {
            super.onStop();
            JRubyAdapter.callMethod(rubyInstance, "onStop" );
          } else {
            super.onStop();
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onStop");
      super.onStop();
    }
  }

  public void onTitleChanged(java.lang.CharSequence title, int color) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_TITLE_CHANGED] != null) {
        super.onTitleChanged(title, color);
        JRubyAdapter.callMethod(callbackProcs[CB_TITLE_CHANGED], "call" , new Object[]{title, color});
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_title_changed}")) {
          super.onTitleChanged(title, color);
          JRubyAdapter.callMethod(rubyInstance, "on_title_changed" , new Object[]{title, color});
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onTitleChanged}")) {
            super.onTitleChanged(title, color);
            JRubyAdapter.callMethod(rubyInstance, "onTitleChanged" , new Object[]{title, color});
          } else {
            super.onTitleChanged(title, color);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onTitleChanged");
      super.onTitleChanged(title, color);
    }
  }

  public boolean onTouchEvent(android.view.MotionEvent event) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_TOUCH_EVENT] != null) {
        super.onTouchEvent(event);
        return (Boolean) JRubyAdapter.callMethod(callbackProcs[CB_TOUCH_EVENT], "call" , event, Boolean.class);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_touch_event}")) {
          super.onTouchEvent(event);
          return (Boolean) JRubyAdapter.callMethod(rubyInstance, "on_touch_event" , event, Boolean.class);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onTouchEvent}")) {
            super.onTouchEvent(event);
            return (Boolean) JRubyAdapter.callMethod(rubyInstance, "onTouchEvent" , event, Boolean.class);
          } else {
            return super.onTouchEvent(event);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onTouchEvent");
      return super.onTouchEvent(event);
    }
  }

  public boolean onTrackballEvent(android.view.MotionEvent event) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_TRACKBALL_EVENT] != null) {
        super.onTrackballEvent(event);
        return (Boolean) JRubyAdapter.callMethod(callbackProcs[CB_TRACKBALL_EVENT], "call" , event, Boolean.class);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_trackball_event}")) {
          super.onTrackballEvent(event);
          return (Boolean) JRubyAdapter.callMethod(rubyInstance, "on_trackball_event" , event, Boolean.class);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onTrackballEvent}")) {
            super.onTrackballEvent(event);
            return (Boolean) JRubyAdapter.callMethod(rubyInstance, "onTrackballEvent" , event, Boolean.class);
          } else {
            return super.onTrackballEvent(event);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onTrackballEvent");
      return super.onTrackballEvent(event);
    }
  }

  public void onWindowAttributesChanged(android.view.WindowManager.LayoutParams params) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_WINDOW_ATTRIBUTES_CHANGED] != null) {
        super.onWindowAttributesChanged(params);
        JRubyAdapter.callMethod(callbackProcs[CB_WINDOW_ATTRIBUTES_CHANGED], "call" , params);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_window_attributes_changed}")) {
          super.onWindowAttributesChanged(params);
          JRubyAdapter.callMethod(rubyInstance, "on_window_attributes_changed" , params);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onWindowAttributesChanged}")) {
            super.onWindowAttributesChanged(params);
            JRubyAdapter.callMethod(rubyInstance, "onWindowAttributesChanged" , params);
          } else {
            super.onWindowAttributesChanged(params);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onWindowAttributesChanged");
      super.onWindowAttributesChanged(params);
    }
  }

  public void onWindowFocusChanged(boolean hasFocus) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_WINDOW_FOCUS_CHANGED] != null) {
        super.onWindowFocusChanged(hasFocus);
        JRubyAdapter.callMethod(callbackProcs[CB_WINDOW_FOCUS_CHANGED], "call" , hasFocus);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_window_focus_changed}")) {
          super.onWindowFocusChanged(hasFocus);
          JRubyAdapter.callMethod(rubyInstance, "on_window_focus_changed" , hasFocus);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onWindowFocusChanged}")) {
            super.onWindowFocusChanged(hasFocus);
            JRubyAdapter.callMethod(rubyInstance, "onWindowFocusChanged" , hasFocus);
          } else {
            super.onWindowFocusChanged(hasFocus);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onWindowFocusChanged");
      super.onWindowFocusChanged(hasFocus);
    }
  }

  public void onUserInteraction() {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_USER_INTERACTION] != null) {
        super.onUserInteraction();
        JRubyAdapter.callMethod(callbackProcs[CB_USER_INTERACTION], "call" );
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_user_interaction}")) {
          super.onUserInteraction();
          JRubyAdapter.callMethod(rubyInstance, "on_user_interaction" );
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onUserInteraction}")) {
            super.onUserInteraction();
            JRubyAdapter.callMethod(rubyInstance, "onUserInteraction" );
          } else {
            super.onUserInteraction();
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onUserInteraction");
      super.onUserInteraction();
    }
  }

  public void onUserLeaveHint() {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_USER_LEAVE_HINT] != null) {
        super.onUserLeaveHint();
        JRubyAdapter.callMethod(callbackProcs[CB_USER_LEAVE_HINT], "call" );
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_user_leave_hint}")) {
          super.onUserLeaveHint();
          JRubyAdapter.callMethod(rubyInstance, "on_user_leave_hint" );
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onUserLeaveHint}")) {
            super.onUserLeaveHint();
            JRubyAdapter.callMethod(rubyInstance, "onUserLeaveHint" );
          } else {
            super.onUserLeaveHint();
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onUserLeaveHint");
      super.onUserLeaveHint();
    }
  }

  public void onAttachedToWindow() {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_ATTACHED_TO_WINDOW] != null) {
        super.onAttachedToWindow();
        JRubyAdapter.callMethod(callbackProcs[CB_ATTACHED_TO_WINDOW], "call" );
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_attached_to_window}")) {
          super.onAttachedToWindow();
          JRubyAdapter.callMethod(rubyInstance, "on_attached_to_window" );
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onAttachedToWindow}")) {
            super.onAttachedToWindow();
            JRubyAdapter.callMethod(rubyInstance, "onAttachedToWindow" );
          } else {
            super.onAttachedToWindow();
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onAttachedToWindow");
      super.onAttachedToWindow();
    }
  }

  public void onBackPressed() {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_BACK_PRESSED] != null) {
        super.onBackPressed();
        JRubyAdapter.callMethod(callbackProcs[CB_BACK_PRESSED], "call" );
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_back_pressed}")) {
          super.onBackPressed();
          JRubyAdapter.callMethod(rubyInstance, "on_back_pressed" );
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onBackPressed}")) {
            super.onBackPressed();
            JRubyAdapter.callMethod(rubyInstance, "onBackPressed" );
          } else {
            super.onBackPressed();
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onBackPressed");
      super.onBackPressed();
    }
  }

  public void onDetachedFromWindow() {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_DETACHED_FROM_WINDOW] != null) {
        super.onDetachedFromWindow();
        JRubyAdapter.callMethod(callbackProcs[CB_DETACHED_FROM_WINDOW], "call" );
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_detached_from_window}")) {
          super.onDetachedFromWindow();
          JRubyAdapter.callMethod(rubyInstance, "on_detached_from_window" );
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onDetachedFromWindow}")) {
            super.onDetachedFromWindow();
            JRubyAdapter.callMethod(rubyInstance, "onDetachedFromWindow" );
          } else {
            super.onDetachedFromWindow();
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onDetachedFromWindow");
      super.onDetachedFromWindow();
    }
  }

  public boolean onKeyLongPress(int keyCode, android.view.KeyEvent event) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_KEY_LONG_PRESS] != null) {
        super.onKeyLongPress(keyCode, event);
        return (Boolean) JRubyAdapter.callMethod(callbackProcs[CB_KEY_LONG_PRESS], "call" , new Object[]{keyCode, event}, Boolean.class);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_key_long_press}")) {
          super.onKeyLongPress(keyCode, event);
          return (Boolean) JRubyAdapter.callMethod(rubyInstance, "on_key_long_press" , new Object[]{keyCode, event}, Boolean.class);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onKeyLongPress}")) {
            super.onKeyLongPress(keyCode, event);
            return (Boolean) JRubyAdapter.callMethod(rubyInstance, "onKeyLongPress" , new Object[]{keyCode, event}, Boolean.class);
          } else {
            return super.onKeyLongPress(keyCode, event);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onKeyLongPress");
      return super.onKeyLongPress(keyCode, event);
    }
  }

  public void onActionModeFinished(android.view.ActionMode mode) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_ACTION_MODE_FINISHED] != null) {
        super.onActionModeFinished(mode);
        JRubyAdapter.callMethod(callbackProcs[CB_ACTION_MODE_FINISHED], "call" , mode);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_action_mode_finished}")) {
          super.onActionModeFinished(mode);
          JRubyAdapter.callMethod(rubyInstance, "on_action_mode_finished" , mode);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onActionModeFinished}")) {
            super.onActionModeFinished(mode);
            JRubyAdapter.callMethod(rubyInstance, "onActionModeFinished" , mode);
          } else {
            super.onActionModeFinished(mode);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onActionModeFinished");
      super.onActionModeFinished(mode);
    }
  }

  public void onActionModeStarted(android.view.ActionMode mode) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_ACTION_MODE_STARTED] != null) {
        super.onActionModeStarted(mode);
        JRubyAdapter.callMethod(callbackProcs[CB_ACTION_MODE_STARTED], "call" , mode);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_action_mode_started}")) {
          super.onActionModeStarted(mode);
          JRubyAdapter.callMethod(rubyInstance, "on_action_mode_started" , mode);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onActionModeStarted}")) {
            super.onActionModeStarted(mode);
            JRubyAdapter.callMethod(rubyInstance, "onActionModeStarted" , mode);
          } else {
            super.onActionModeStarted(mode);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onActionModeStarted");
      super.onActionModeStarted(mode);
    }
  }

  public void onAttachFragment(android.app.Fragment fragment) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_ATTACH_FRAGMENT] != null) {
        super.onAttachFragment(fragment);
        JRubyAdapter.callMethod(callbackProcs[CB_ATTACH_FRAGMENT], "call" , fragment);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_attach_fragment}")) {
          super.onAttachFragment(fragment);
          JRubyAdapter.callMethod(rubyInstance, "on_attach_fragment" , fragment);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onAttachFragment}")) {
            super.onAttachFragment(fragment);
            JRubyAdapter.callMethod(rubyInstance, "onAttachFragment" , fragment);
          } else {
            super.onAttachFragment(fragment);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onAttachFragment");
      super.onAttachFragment(fragment);
    }
  }

  public android.view.View onCreateView(android.view.View parent, java.lang.String name, android.content.Context context, android.util.AttributeSet attrs) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_CREATE_VIEW] != null) {
        super.onCreateView(parent, name, context, attrs);
        return (android.view.View) JRubyAdapter.callMethod(callbackProcs[CB_CREATE_VIEW], "call" , new Object[]{parent, name, context, attrs}, android.view.View.class);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_create_view}")) {
          super.onCreateView(parent, name, context, attrs);
          return (android.view.View) JRubyAdapter.callMethod(rubyInstance, "on_create_view" , new Object[]{parent, name, context, attrs}, android.view.View.class);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onCreateView}")) {
            super.onCreateView(parent, name, context, attrs);
            return (android.view.View) JRubyAdapter.callMethod(rubyInstance, "onCreateView" , new Object[]{parent, name, context, attrs}, android.view.View.class);
          } else {
            return super.onCreateView(parent, name, context, attrs);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onCreateView");
      return super.onCreateView(parent, name, context, attrs);
    }
  }

  public boolean onKeyShortcut(int keyCode, android.view.KeyEvent event) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_KEY_SHORTCUT] != null) {
        super.onKeyShortcut(keyCode, event);
        return (Boolean) JRubyAdapter.callMethod(callbackProcs[CB_KEY_SHORTCUT], "call" , new Object[]{keyCode, event}, Boolean.class);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_key_shortcut}")) {
          super.onKeyShortcut(keyCode, event);
          return (Boolean) JRubyAdapter.callMethod(rubyInstance, "on_key_shortcut" , new Object[]{keyCode, event}, Boolean.class);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onKeyShortcut}")) {
            super.onKeyShortcut(keyCode, event);
            return (Boolean) JRubyAdapter.callMethod(rubyInstance, "onKeyShortcut" , new Object[]{keyCode, event}, Boolean.class);
          } else {
            return super.onKeyShortcut(keyCode, event);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onKeyShortcut");
      return super.onKeyShortcut(keyCode, event);
    }
  }

  public android.view.ActionMode onWindowStartingActionMode(android.view.ActionMode.Callback callback) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_WINDOW_STARTING_ACTION_MODE] != null) {
        super.onWindowStartingActionMode(callback);
        return (android.view.ActionMode) JRubyAdapter.callMethod(callbackProcs[CB_WINDOW_STARTING_ACTION_MODE], "call" , callback, android.view.ActionMode.class);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_window_starting_action_mode}")) {
          super.onWindowStartingActionMode(callback);
          return (android.view.ActionMode) JRubyAdapter.callMethod(rubyInstance, "on_window_starting_action_mode" , callback, android.view.ActionMode.class);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onWindowStartingActionMode}")) {
            super.onWindowStartingActionMode(callback);
            return (android.view.ActionMode) JRubyAdapter.callMethod(rubyInstance, "onWindowStartingActionMode" , callback, android.view.ActionMode.class);
          } else {
            return super.onWindowStartingActionMode(callback);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onWindowStartingActionMode");
      return super.onWindowStartingActionMode(callback);
    }
  }

  public boolean onGenericMotionEvent(android.view.MotionEvent event) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_GENERIC_MOTION_EVENT] != null) {
        super.onGenericMotionEvent(event);
        return (Boolean) JRubyAdapter.callMethod(callbackProcs[CB_GENERIC_MOTION_EVENT], "call" , event, Boolean.class);
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_generic_motion_event}")) {
          super.onGenericMotionEvent(event);
          return (Boolean) JRubyAdapter.callMethod(rubyInstance, "on_generic_motion_event" , event, Boolean.class);
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onGenericMotionEvent}")) {
            super.onGenericMotionEvent(event);
            return (Boolean) JRubyAdapter.callMethod(rubyInstance, "onGenericMotionEvent" , event, Boolean.class);
          } else {
            return super.onGenericMotionEvent(event);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onGenericMotionEvent");
      return super.onGenericMotionEvent(event);
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

  public void onApplyThemeResource(android.content.res.Resources.Theme theme, int resid, boolean first) {
    if (JRubyAdapter.isInitialized()) {
      if (callbackProcs != null && callbackProcs[CB_APPLY_THEME_RESOURCE] != null) {
        super.onApplyThemeResource(theme, resid, first);
        JRubyAdapter.callMethod(callbackProcs[CB_APPLY_THEME_RESOURCE], "call" , new Object[]{theme, resid, first});
      } else {
        String rubyClassName = Script.toCamelCase(scriptName);
        if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :on_apply_theme_resource}")) {
          super.onApplyThemeResource(theme, resid, first);
          JRubyAdapter.callMethod(rubyInstance, "on_apply_theme_resource" , new Object[]{theme, resid, first});
        } else {
          if ((Boolean)JRubyAdapter.runScriptlet("defined?(" + rubyClassName + ") == 'constant' && " + rubyClassName + ".instance_methods(false).any?{|m| m.to_sym == :onApplyThemeResource}")) {
            super.onApplyThemeResource(theme, resid, first);
            JRubyAdapter.callMethod(rubyInstance, "onApplyThemeResource" , new Object[]{theme, resid, first});
          } else {
            super.onApplyThemeResource(theme, resid, first);
          }
        }
      }
    } else {
      Log.i("Method called before JRuby runtime was initialized: " + getClass().getSimpleName() + "#onApplyThemeResource");
      super.onApplyThemeResource(theme, resid, first);
    }
  }

}
