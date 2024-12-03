package mygame;

import com.jme3.scene.control.AbstractControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

public class PageControl extends AbstractControl {
    private Page page;

    public PageControl(Page page) {
        this.page = page;
    }

    public Page getPage() {
        return page;
    }

    @Override
    protected void controlUpdate(float tpf) {
        // No update logic needed unless you have animations or effects
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // No rendering logic needed here
    }
}