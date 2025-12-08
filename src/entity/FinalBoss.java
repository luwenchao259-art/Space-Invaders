package entity;

import engine.DrawManager;
import engine.Cooldown;
import engine.Core;
import screen.GameScreen;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class FinalBoss extends Entity implements BossEntity{

    private int healPoint;
    private int maxHp;
    private final int pointValue;
    private boolean isDestroyed;
    /** for move pattern */
    private int zigDirection = 1;
    /** for move pattern */
    private boolean goingDown = true;

    private Cooldown animationCooldown;
    /** Shoot1's cool down */
    private Cooldown shootCooldown1;
    /** Shoot2's cool down */
    private Cooldown shootCooldown2;
    /** Shoot3's cool down */
    private Cooldown shootCooldown3;
    private int screenWidth;
    private int screenHeight;

    /** Flash effect when damaged. */
    private Cooldown damageFlashCooldown;
    /** Original color before damage flash. */
    private final Color originalColor = Color.RED;


    /** basic attribute of final boss */

    public FinalBoss(int positionX, int positionY, int screenWidth, int screenHeight){                                                                          

        super(positionX,positionY,140,110, Color.RED);
        this.healPoint = 20;
        this.maxHp = healPoint;
        this.pointValue = 1000;
        this.spriteType = DrawManager.SpriteType.EnemyShipSpecial;
        this.isDestroyed = false;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;


        this.animationCooldown = Core.getCooldown(500);
        this.shootCooldown1 = Core.getCooldown(5000);
        this.shootCooldown2 = Core.getCooldown(400);
        this.shootCooldown3 = Core.getCooldown(300);
        this.damageFlashCooldown = Core.getCooldown(200);


    }

    /** for vibrant moving with final boss
     * final boss spritetype is the same with special enemy and enemyshipA, because final boss spritetype have not yet implemented
     * becasue final boss is single object, moving and shooting pattern are included in update methods
     */
    @Override
    public void update(){
        if(this.animationCooldown.checkFinished()){
            this.animationCooldown.reset();

            if(this.spriteType == DrawManager.SpriteType.EnemyShipSpecial){
                this.spriteType = DrawManager.SpriteType.EnemyShipA1;
            } else{
                this.spriteType = DrawManager.SpriteType.EnemyShipSpecial;
            }
        }
        

        
        movePattern();
        
        // Update damage flash effect
        if (!this.damageFlashCooldown.checkFinished()) {
            // Flash white when damaged
            this.color = Color.WHITE;
        } else {
            // Restore original red color
            this.color = originalColor;
        }

    }

    /** decrease boss' healpoint and trigger damage flash effect */
    @Override
    public void takeDamage(int damage){
        this.healPoint -= damage;
        this.damageFlashCooldown.reset(); // Trigger damage flash effect
        if(this.healPoint <= 0){
            this.destroy();
        }
    }

    @Override
    public int getHealPoint(){
        return this.healPoint;
    }

    public int getMaxHp(){
        return  this.maxHp;
    }

    @Override
    public int getPointValue(){
        return this.pointValue;
    }

    /** movement pattern of final boss */
    public void movePattern(){
        if(this.healPoint > this.maxHp/2){
            this.move(0,0);
        }
        else if (this.healPoint > this.maxHp/4){
            this.moveZigzag(4,3);
        }
        else {
            this.moveZigzag(2,1);
        }
    }

    /** move zigzag */
    public void moveZigzag(int zigSpeed, int vertSpeed){
        this.positionX += (this.zigDirection * zigSpeed);
        if(this.positionX <= 0 || this.positionX >= this.screenWidth-this.width){
            this.zigDirection *= -1;
        }

        if(goingDown) {
            this.positionY += vertSpeed;
            if (this.positionY >= screenHeight/2 - this.height) goingDown = false;
        }
        else {
            this.positionY -= vertSpeed;
            if(this.positionY <= 0) goingDown = true;
        }
    }

    /** move simple */
    @Override
    public void move(int distanceX, int distanceY){
        this.positionX += distanceX;
        this.positionY += distanceY;
    }

    /** shooting pattern of final boss */


    /** first shooting pattern of final boss */
    public Set<BossBullet> shoot1(){
        if(this.shootCooldown1.checkFinished()){
            this.shootCooldown1.reset();
            Set<BossBullet> bullets = new HashSet<>();
            int arr[] = {0,1,-1,2,-2};
            for (int i : arr){
                BossBullet bullet = new BossBullet(this.getPositionX() + this.getWidth() / 2 - 3,this.getPositionY() + this.getHeight(), i,4,6,10,Color.yellow);
                bullets.add(bullet);
            }
            return bullets;
        }
        return java.util.Collections.emptySet();
    }
    /** second shooting pattern of final boss */
    public Set<BossBullet> shoot2() {
        if (this.shootCooldown2.checkFinished()) {
            this.shootCooldown2.reset();
            Set<BossBullet> bullets = new HashSet<>();
            int randomX = (int) (Math.random() * screenWidth);
            BossBullet bullet = new BossBullet(randomX, 1, 0, 2,6,10,Color.yellow);
            bullets.add(bullet);
            return bullets;
        }
        return java.util.Collections.emptySet();
    }
    /** third shooting pattern of final boss */
    public Set<BossBullet> shoot3() {
        Set<BossBullet> bullets = new HashSet<>();
        if (this.shootCooldown3.checkFinished()) {
            this.shootCooldown3.reset();
//            if (!(this.getPositionX() == 0 || this.getPositionX() == 400)){
                BossBullet bullet1 = new BossBullet(this.getPositionX() + this.getWidth() / 2 - 3 + 70, this.positionY, 0, 5,6,10,Color.blue);
                BossBullet bullet2 = new BossBullet(this.getPositionX() + this.getWidth() / 2 - 3 - 70, this.positionY, 0, 5,6,10,Color.blue);
                bullets.add(bullet1);
                bullets.add(bullet2);
//            }
        }
        return bullets;
    }





    /** flag final boss' destroy */
    @Override
    public void destroy(){
        if(!this.isDestroyed){
            this.isDestroyed = true;
            this.spriteType = DrawManager.SpriteType.Explosion;
        }
    }

    /** check final boss' destroy */
    @Override
    public boolean isDestroyed(){
        return this.isDestroyed;
    }

    @Override
    public void draw(DrawManager drawManager) {
        drawManager.drawEntity(this, this.positionX, this.positionY);
        // Draw health bar above the boss
        if (!this.isDestroyed) {
            drawManager.drawBossHealthBar(this, this.positionX, this.positionY, this.healPoint, this.maxHp);
        }
    }
}