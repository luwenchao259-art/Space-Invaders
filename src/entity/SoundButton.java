package entity;

import java.awt.Color;
import engine.DrawManager.SpriteType;

/**
 *  Implements a sound button, to change sound state such as on/off.
 * 
 */
public class SoundButton extends Entity {

    /** State of the Sound on/off */
    private static boolean isSoundOn = true;

    /**
     * Constructor, establishes the button's properties.
     * 
     * @param positionX
     *            Initial position of the button in the X axis.
     * @param positionY
     *            Initial position of the button in the Y axis.
     */

    public SoundButton(final int positionX, final int positionY) {
        super(positionX, positionY, 32, 32, Color.WHITE);

        this.spriteType = SpriteType.SoundOn;
    }

    /**
     *  Getter the state of the sound.
     * @return isSoundOn
     */
    public boolean getIsSoundOn() {
        return isSoundOn;
    }

    /**
     *  Change the color of the button.
     * @param color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Change the sound state and the button sprite.
     */
    public void changeSoundState() {
        if (isSoundOn) {
            this.spriteType = SpriteType.SoundOff;
            isSoundOn = false;
        } else {
            this.spriteType = SpriteType.SoundOn;
            isSoundOn = true;
        }
    }
}