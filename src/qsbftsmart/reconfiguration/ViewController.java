/**
Copyright (c) 2007-2013 Alysson Bessani, Eduardo Alchieri, Paulo Sousa, and the authors indicated in the @author tags

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package qsbftsmart.reconfiguration;

import qsbftsmart.reconfiguration.views.DefaultViewStorage;
import qsbftsmart.reconfiguration.util.TOMConfiguration;
import qsbftsmart.reconfiguration.views.View;
import qsbftsmart.reconfiguration.views.ViewStorage;
import qsbftsmart.tom.util.KeyLoader;

import java.net.SocketAddress;

/**
 *
 * @author eduardo
 */
public class ViewController {

    protected qsbftsmart.reconfiguration.views.View lastView = null;
    protected qsbftsmart.reconfiguration.views.View currentView = null;
    private TOMConfiguration staticConf;
    private ViewStorage viewStore;

    public ViewController(int procId, KeyLoader loader) {
        this.staticConf = new TOMConfiguration(procId, loader);
    }

    
    public ViewController(int procId, String configHome, KeyLoader loader) {
        this.staticConf = new TOMConfiguration(procId, configHome, loader);
    }

    
    public final ViewStorage getViewStore() {
        if (this.viewStore == null) {
            String className = staticConf.getViewStoreClass();
            try {
                this.viewStore = (ViewStorage) Class.forName(className).newInstance();
            } catch (Exception e) {
                this.viewStore = new DefaultViewStorage(this.staticConf.getConfigHome(), this.staticConf.getInstanceId());
            }

        }
        return this.viewStore;
    }

    public qsbftsmart.reconfiguration.views.View getCurrentView(){
        if(this.currentView == null){
             this.currentView = getViewStore().readView();
        }
        return this.currentView;
    }
    
    public qsbftsmart.reconfiguration.views.View getLastView(){
        return this.lastView;
    }
    
    public SocketAddress getRemoteAddress(int id) {
        return getCurrentView().getAddress(id);
    }
    
    public void reconfigureTo(View newView) {
        this.lastView = this.currentView;
        this.currentView = newView;
    }

    public TOMConfiguration getStaticConf() {
        return staticConf;
    }

    public boolean isCurrentViewMember(int id) {
        return getCurrentView().isMember(id);
    }

    public int getCurrentViewId() {
        return getCurrentView().getId();
    }

    public int getCurrentViewF() {
        return getCurrentView().getF();
    }

    public int getCurrentViewN() {
        return getCurrentView().getN();
    }

    public int getCurrentViewPos(int id) {
        return getCurrentView().getPos(id);
    }

    public int[] getCurrentViewProcesses() {
        return getCurrentView().getProcesses();
    }
}