package screen;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import engine.Cooldown;
import engine.Core;
import engine.GameSettings;
import engine.GameState;
import engine.GameTimer;
import engine.AchievementManager;
import engine.ItemHUDManager;
import entity.*;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import engine.level.Level;

/**
 * Implements the game screen, where the action happens.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class GameScreen extends Screen {

	/** Milliseconds until the screen accepts user input. */
	private static final int INPUT_DELAY = 6000;
	/** Bonus score for each life remaining at the end of the level. */
	private static final int LIFE_SCORE = 100;
	/** Minimum time between bonus ship's appearances. */
	private static final int BONUS_SHIP_INTERVAL = 20000;
	/** Maximum variance in the time between bonus ship's appearances. */
	private static final int BONUS_SHIP_VARIANCE = 10000;
	/** Time until bonus ship explosion disappears. */
	private static final int BONUS_SHIP_EXPLOSION = 500;
	/** Time until bonus ship explosion disappears. */
	private static final int BOSS_EXPLOSION = 600;
	/** Time from finishing the level to screen change. */
	private static final int SCREEN_CHANGE_INTERVAL = 1500;
	/** Height of the interface separation line. */
	private static final int SEPARATION_LINE_HEIGHT = 40;
	/** Height of the items separation line (above items). */
	private static final int ITEMS_SEPARATION_LINE_HEIGHT = 400;

	/** Current game difficulty settings. */
	private GameSettings gameSettings;
	/** Current difficulty level number. */
	private int level;
	/** Formation of enemy ships. */
	private EnemyShipFormation enemyShipFormation;
	/** Formation of special enemy ships. */
	private EnemyShipSpecialFormation enemyShipSpecialFormation;
	/** Player's ship. */
	private Ship ship;
	/** Bonus enemy ship that appears sometimes. */
	private EnemyShip enemyShipSpecial;
	/** Minimum time between bonus ship appearances. */
	private Cooldown enemyShipSpecialCooldown;
	/** Time until bonus ship explosion disappears. */

	/** team drawing may implement */
	private FinalBoss finalBoss;

	private Cooldown enemyShipSpecialExplosionCooldown;
	/** Time until Boss explosion disappears. */
	private Cooldown bossExplosionCooldown;
	/** Time from finishing the level to screen change. */
	private Cooldown screenFinishedCooldown;
	/** OmegaBoss */
	private MidBoss omegaBoss;
	/** Set of all bullets fired by on-screen ships. */
	private Set<Bullet> bullets;
	/** Set of all dropItems dropped by on screen ships. */
	private Set<DropItem> dropItems;
	/** Current score. */
	private int score;
	/** Player lives left. */
	private int lives;
	/** Total bullets shot by the player. */
	private int bulletsShot;
	/** Total ships destroyed by the player. */
	private int shipsDestroyed;
	/** Moment the game starts. */
	private long gameStartTime;
	/** Checks if the level is finished. */
	private boolean levelFinished;
	/** Checks if a bonus life is received. */
	private boolean bonusLife;
  /** Maximum number of lives. */
	private int maxLives;
	/** Current coin. */
	private int coin;
	/** bossBullets carry bullets which Boss fires */
	private Set<BossBullet> bossBullets;
	/** Is the bullet on the screen erased */
  private boolean is_cleared = false;
  /** Timer to track elapsed time. */
  private GameTimer gameTimer;
  /** Elapsed time since the game started. */
  private long elapsedTime;
  // Achievement popup
  private String achievementText;
  private Cooldown achievementPopupCooldown;
  /** Health change popup. */
  private String healthPopupText;
  private Cooldown healthPopupCooldown;

	    private GameState gameState;
	
	    /**
	     * Constructor, establishes the properties of the screen.
	     * 
	     * @param gameState
	     *            Current game state.	 * @param level
	 *            Current level settings.
	 * @param bonusLife
	 *            Checks if a bonus life is awarded this level.
	 * @param maxLives
	 *            Maximum number of lives.
	 * @param width
	 *            Screen width.
	 * @param height
	 *            Screen height.
	 * @param fps
	 *            Frames per second, frame rate at which the game is run.
	 */
	public GameScreen(final GameState gameState,
			final Level level, final boolean bonusLife, final int maxLives,
			final int width, final int height, final int fps) {
		super(width, height, fps);

		this.gameSettings = new GameSettings(level.getFormationWidth(), level.getFormationHeight(), level.getBaseSpeed(), level.getShootingFrecuency());
		this.bonusLife = bonusLife;
		this.maxLives = maxLives;
		        this.level = gameState.getLevel();
		        this.score = gameState.getScore();
		        this.lives = gameState.getLivesRemaining();
		        this.gameState = gameState;		if (this.bonusLife)
			this.lives++;
		this.bulletsShot = gameState.getBulletsShot();
		this.shipsDestroyed = gameState.getShipsDestroyed();
	}

	/**
	 * Initializes basic screen properties, and adds necessary elements.
	 */
	public final void initialize() {
		super.initialize();
		/** Initialize the bullet Boss fired */
		this.bossBullets = new HashSet<>();


		enemyShipFormation = new EnemyShipFormation(this.gameSettings);
		enemyShipFormation.attach(this);
		this.ship = new Ship(this.width / 2, ITEMS_SEPARATION_LINE_HEIGHT - 50);
		// special enemy initial
		enemyShipSpecialFormation = new EnemyShipSpecialFormation(this.gameSettings,
				Core.getVariableCooldown(BONUS_SHIP_INTERVAL, BONUS_SHIP_VARIANCE),
				Core.getCooldown(BONUS_SHIP_EXPLOSION));
		enemyShipSpecialFormation.attach(this);
		this.bossExplosionCooldown = Core
				.getCooldown(BOSS_EXPLOSION);
		this.screenFinishedCooldown = Core.getCooldown(SCREEN_CHANGE_INTERVAL);
		this.bullets = new HashSet<Bullet>();
        this.dropItems = new HashSet<DropItem>();

		// Special input delay / countdown.
		this.gameStartTime = System.currentTimeMillis();
		this.inputDelay = Core.getCooldown(INPUT_DELAY);
		this.inputDelay.reset();

		// Initializing Middle Boss
		this.omegaBoss = new OmegaBoss(Color.ORANGE);
		omegaBoss.attach(this);
		this.gameTimer = new GameTimer();
        this.elapsedTime = 0;
	}

	/**
	 * Starts the action.
	 * 
	 * @return Next screen code.
	 */
	public final int run() {
		super.run();

		this.score += LIFE_SCORE * (this.lives - 1);
		this.logger.info("Screen cleared with a score of " + this.score);

		return this.returnCode;
	}

	/**
	 * Updates the elements on screen and checks for events.
	 */
	protected final void update() {
		super.update();

		if (this.inputDelay.checkFinished() && !this.levelFinished) {

			/** spawn final boss to check object (for test) */
			if(this.finalBoss == null){
				this.finalBoss = new FinalBoss(this.width/2-50,50,this.width,this.height);
				this.logger.info("Final Boss created.");
			}
			if (this.finalBoss != null && !this.finalBoss.isDestroyed()) {
			/** called the boss shoot logic */
					if(this.finalBoss.getHealPoint() > this.finalBoss.getMaxHp() / 4) {
						bossBullets.addAll(this.finalBoss.shoot1());
						bossBullets.addAll(this.finalBoss.shoot2());

					}
					/** Is the bullet on the screen erased */
					else {
						if (!is_cleared){
							bossBullets.clear();
							is_cleared = true;
							logger.info("boss is angry");
						}
						else {

							bossBullets.addAll(this.finalBoss.shoot3());
						}
					}

				/** bullets to erase */
				Set<BossBullet> bulletsToRemove = new HashSet<>();

				for (BossBullet b : bossBullets) {
					b.update();
					/** If the bullet goes off the screen */
					if (b.isOffScreen(width, height)) {
						/** bulletsToRemove carry bullet */
						bulletsToRemove.add(b);
					}
					/** If the bullet collides with ship */
					else if (this.checkCollision(b,this.ship)) {
						                        if (!this.ship.isDestroyed()) {
						                            this.ship.destroy();
						                            this.lives--;
						                            this.logger.info("Hit on player ship, " + this.lives
						                                    + " lives remaining.");
						}
						bulletsToRemove.add(b);
					}
				}
				/** all bullets are removed */
				bossBullets.removeAll(bulletsToRemove);
			}

			if (!this.gameTimer.isRunning()) {
                this.gameTimer.start();
            }
			if (!this.ship.isDestroyed()) {
				boolean moveRight = inputManager.isKeyDown(KeyEvent.VK_RIGHT)
						|| inputManager.isKeyDown(KeyEvent.VK_D);
				boolean moveLeft = inputManager.isKeyDown(KeyEvent.VK_LEFT)
						|| inputManager.isKeyDown(KeyEvent.VK_A);
				boolean moveUp = inputManager.isKeyDown(KeyEvent.VK_UP)
						|| inputManager.isKeyDown(KeyEvent.VK_W);
				boolean moveDown = inputManager.isKeyDown(KeyEvent.VK_DOWN)
						|| inputManager.isKeyDown(KeyEvent.VK_S);

				boolean isRightBorder = this.ship.getPositionX()
						+ this.ship.getWidth() + this.ship.getSpeed() > this.width - 1;
				boolean isLeftBorder = this.ship.getPositionX()
						- this.ship.getSpeed() < 1;
                boolean isUpBorder = this.ship.getPositionY()
                        - this.ship.getSpeed() < SEPARATION_LINE_HEIGHT;
                boolean isDownBorder = this.ship.getPositionY()
                        + this.ship.getHeight() + this.ship.getSpeed() > ITEMS_SEPARATION_LINE_HEIGHT;

				if (moveRight && !isRightBorder) {
					this.ship.moveRight();
				}
				if (moveLeft && !isLeftBorder) {
					this.ship.moveLeft();
				}
                if (moveUp && !isUpBorder) {
                    this.ship.moveUp();
                }
                if (moveDown && !isDownBorder) {
                    this.ship.moveDown();
                }

				if (inputManager.isKeyDown(KeyEvent.VK_SPACE))
                    if (this.ship.shoot(this.bullets)) {
						this.bulletsShot++;
						AchievementManager.getInstance().onShotFired();
					}
			}
			if (this.omegaBoss != null){
				if(!this.omegaBoss.isDestroyed()) {
					this.omegaBoss.update();
				}
				else if (this.bossExplosionCooldown.checkFinished()) {
					this.omegaBoss = null;
				}
			}
			this.ship.update();
			if (!DropItem.isTimeFreezeActive()) {
				this.enemyShipFormation.update();
				this.enemyShipFormation.shoot(this.bullets);
			}
			/** when the final boss is at the field */
			if(this.finalBoss != null && !this.finalBoss.isDestroyed()){
				this.finalBoss.update();
			}
			// special enemy update
			this.enemyShipSpecialFormation.update();
		}
		if (this.gameTimer.isRunning()) {
            this.elapsedTime = this.gameTimer.getElapsedTime();
				AchievementManager.getInstance().onTimeElapsedSeconds((int)(this.elapsedTime / 1000));
        }
        cleanItems();
		manageCollisions();
		cleanBullets();
		draw();

		        if ((this.enemyShipFormation.isEmpty() || this.lives == 0)
		                && !this.levelFinished) {
		            this.levelFinished = true;
		            this.screenFinishedCooldown.reset();
		            if (this.gameTimer.isRunning()) {
		                this.gameTimer.stop();
		            }
		
		            if (this.lives > 0) {
		                if (this.level == 1) {
		                    AchievementManager.getInstance().unlockAchievement("Beginner");
		                } else if (this.level == 3) {
		                    AchievementManager.getInstance().unlockAchievement("Intermediate");
		                }
		            }
		        }
		if (this.levelFinished && this.screenFinishedCooldown.checkFinished())
			this.isRunning = false;

	}

	/**
	 * Draws the elements associated with the screen.
	 */
	private void draw() {
		drawManager.initDrawing(this);

		drawManager.drawEntity(this.ship, this.ship.getPositionX(),
				this.ship.getPositionY());
		// special enemy draw
		enemyShipSpecialFormation.draw();

		/** draw final boss at the field */
		/** draw final boss bullets */
		if(this.finalBoss != null && !this.finalBoss.isDestroyed()){
			for (BossBullet bossBullet : bossBullets) {
				drawManager.drawEntity(bossBullet, bossBullet.getPositionX(), bossBullet.getPositionY());
			}
			drawManager.drawEntity(finalBoss, finalBoss.getPositionX(), finalBoss.getPositionY());
		}

		enemyShipFormation.draw();

		if(this.omegaBoss != null) {
			this.omegaBoss.draw(drawManager);
		}

		for (Bullet bullet : this.bullets)
			drawManager.drawEntity(bullet, bullet.getPositionX(),
					bullet.getPositionY());

    for (DropItem dropItem : this.dropItems)
      drawManager.drawEntity(dropItem, dropItem.getPositionX(), dropItem.getPositionY());

		// Interface.
		drawManager.drawScore(this, this.score);
		drawManager.drawCoin(this,this.coin);
		drawManager.drawLives(this, this.lives);
		drawManager.drawTime(this, this.elapsedTime);
		drawManager.drawItemsHUD(this);
        drawManager.drawLevel(this, this.level);
        drawManager.drawHorizontalLine(this, SEPARATION_LINE_HEIGHT - 1);
		drawManager.drawHorizontalLine(this, ITEMS_SEPARATION_LINE_HEIGHT);

        if (this.achievementText != null && !this.achievementPopupCooldown.checkFinished()) {
            drawManager.drawAchievementPopup(this, this.achievementText);
        } else {
            this.achievementText = null; // clear once expired
        }


        // Health notification popup
        if(this.healthPopupText != null && !this.healthPopupCooldown.checkFinished()) {
            drawManager.drawHealthPopup(this, this.healthPopupText);
        } else {
            this.healthPopupText = null;
        }

        // Countdown to game start.
		if (!this.inputDelay.checkFinished()) {
			int countdown = (int) ((INPUT_DELAY
					- (System.currentTimeMillis()
					- this.gameStartTime)) / 1000);
			drawManager.drawCountDown(this, this.level, countdown,
					this.bonusLife);
			drawManager.drawHorizontalLine(this, this.height / 2 - this.height
					/ 12);
			drawManager.drawHorizontalLine(this, this.height / 2 + this.height
					/ 12);
		}

		drawManager.completeDrawing(this);
	}

	/**
	 * Cleans bullets that go off screen.
	 */
	private void cleanBullets() {
		Set<Bullet> recyclable = new HashSet<Bullet>();
		for (Bullet bullet : this.bullets) {
			bullet.update();
			if (bullet.getPositionY() < SEPARATION_LINE_HEIGHT
					|| bullet.getPositionY() > this.height)
				recyclable.add(bullet);
		}
		this.bullets.removeAll(recyclable);
		BulletPool.recycle(recyclable);
	}

    /**
     * Cleans Items that go off screen.
     */

    private void cleanItems() {
        Set<DropItem> recyclable = new HashSet<DropItem>();
        for (DropItem dropItem : this.dropItems) {
            dropItem.update();
            if (dropItem.getPositionY() < SEPARATION_LINE_HEIGHT
                    || dropItem.getPositionY() > this.height)
                recyclable.add(dropItem);
        }
        this.dropItems.removeAll(recyclable);
        ItemPool.recycle(recyclable);
    }

	/**
	 * Manages collisions between bullets and ships.
	 */
	private void manageCollisions() {
		Set<Bullet> recyclable = new HashSet<Bullet>();
		for (Bullet bullet : this.bullets)
			if (bullet.getSpeed() > 0) {
				if (checkCollision(bullet, this.ship) && !this.levelFinished) {
					recyclable.add(bullet);
					if (!this.ship.isInvincible()) {
						if (!this.ship.isDestroyed()) {
							                           this.ship.destroy();
							                           this.lives--;
							                           showHealthPopup("-1 Health");							this.logger.info("Hit on player ship, " + this.lives
									+ " lives remaining.");
						}
					}
				}
			} else {
				for (EnemyShip enemyShip : this.enemyShipFormation)
					if (!enemyShip.isDestroyed()
							&& checkCollision(bullet, enemyShip)) {
						this.score += enemyShip.getPointValue();
                        this.coin += (enemyShip.getPointValue()/10);
						this.shipsDestroyed++;
						this.enemyShipFormation.destroy(enemyShip);
						AchievementManager.getInstance().onEnemyDefeated();
                        DropItem.ItemType droppedType = DropItem.getRandomItemType(0.3);
                        if (droppedType != null) {
                            final int ITEM_DROP_SPEED = 2;

                            DropItem newDropItem = ItemPool.getItem(
                                    enemyShip.getPositionX() + enemyShip.getWidth() / 2,
                                    enemyShip.getPositionY() + enemyShip.getHeight() / 2,
                                    ITEM_DROP_SPEED,
                                    droppedType
                            );
                            this.dropItems.add(newDropItem);
                            this.logger.info("An item (" + droppedType + ") dropped");
                        }

						if (!bullet.penetration()) {
							recyclable.add(bullet);
							break;
						}

					}

				// special enemy bullet event
				for (EnemyShip enemyShipSpecial : this.enemyShipSpecialFormation)
					if (enemyShipSpecial != null && !enemyShipSpecial.isDestroyed()
							&& checkCollision(bullet, enemyShipSpecial)) {
						this.score += enemyShipSpecial.getPointValue();
						this.coin += (enemyShipSpecial.getPointValue()/10);
						this.shipsDestroyed++;
						this.enemyShipSpecialFormation.destroy(enemyShipSpecial);
						recyclable.add(bullet);
				}
				if (this.omegaBoss != null
						&& !this.omegaBoss.isDestroyed()
						&& checkCollision(bullet, this.omegaBoss)) {
					this.omegaBoss.takeDamage(2);
					if(this.omegaBoss.getHealPoint() <= 0) {
						this.shipsDestroyed++;
						this.score += this.omegaBoss.getPointValue();
						                        this.coin += (this.omegaBoss.getPointValue()/10);
						                        this.omegaBoss.destroy();
						                        AchievementManager.getInstance().unlockAchievement("Boss Slayer");						this.bossExplosionCooldown.reset();
					}
					recyclable.add(bullet);
				}

				/** when final boss collide with bullet */
				if(this.finalBoss != null && !this.finalBoss.isDestroyed() && checkCollision(bullet,this.finalBoss)){
					this.finalBoss.takeDamage(1);
					                    if(this.finalBoss.getHealPoint() <= 0){
					                        this.score += this.finalBoss.getPointValue();
					                        this.coin += (this.finalBoss.getPointValue()/10);
					                        AchievementManager.getInstance().unlockAchievement("Boss Slayer");
					                    }					recyclable.add(bullet);
				}

			}
		this.bullets.removeAll(recyclable);
		BulletPool.recycle(recyclable);

        Set<DropItem> acquiredDropItems = new HashSet<DropItem>();

        if (!this.levelFinished && !this.ship.isDestroyed()) {
            for (DropItem dropItem : this.dropItems) {

                if (checkCollision(this.ship, dropItem)) {
                    this.logger.info("Player acquired dropItem: " + dropItem.getItemType());
                    
                    // Add item to HUD display
                    ItemHUDManager.getInstance().addDroppedItem(dropItem.getItemType());
                    
                    switch (dropItem.getItemType()) {
                        case Heal:
                            gainLife();
                            break;
                        case Shield:
                            ship.activateInvincibility(5000); // 5 seconds of invincibility
                            break;
						case Stop:
							DropItem.applyTimeFreezeItem(3000);
							break;
						case Push:
							DropItem.PushbackItem(this.enemyShipFormation,20);
							break;
                        case Explode:
                            int destroyedEnemy = this.enemyShipFormation.destroyAll();
                            this.score += destroyedEnemy * 5;
                            break;
                        case Slow:
                            enemyShipFormation.activateSlowdown();
                            this.logger.info("Enemy formation slowed down!");
                            break;
                        default:
                            // For other dropItem types. Free to add!
                            break;
                    }
                    acquiredDropItems.add(dropItem);
                }
            }
            this.dropItems.removeAll(acquiredDropItems);
            ItemPool.recycle(acquiredDropItems);
        }
    }




	/**
	 * Checks if two entities are colliding.
	 * 
	 * @param a
	 *            First entity, the bullet.
	 * @param b
	 *            Second entity, the ship.
	 * @return Result of the collision test.
	 */
	private boolean checkCollision(final Entity a, final Entity b) {
		// Calculate center point of the entities in both axis.
		int centerAX = a.getPositionX() + a.getWidth() / 2;
		int centerAY = a.getPositionY() + a.getHeight() / 2;
		int centerBX = b.getPositionX() + b.getWidth() / 2;
		int centerBY = b.getPositionY() + b.getHeight() / 2;
		// Calculate maximum distance without collision.
		int maxDistanceX = a.getWidth() / 2 + b.getWidth() / 2;
		int maxDistanceY = a.getHeight() / 2 + b.getHeight() / 2;
		// Calculates distance.
		int distanceX = Math.abs(centerAX - centerBX);
		int distanceY = Math.abs(centerAY - centerBY);

		return distanceX < maxDistanceX && distanceY < maxDistanceY;
	}

    /**
     * Shows an achievement popup message on the HUD.
     *
     * @param message
     *      Text to display in the popup.
     */
    public void showAchievement(String message) {
        this.achievementText = message;
        this.achievementPopupCooldown = Core.getCooldown(2500); // Show for 2.5 seconds
        this.achievementPopupCooldown.reset();
    }

    /**
     * Displays a notification popup when the player gains or loses health
     *
     * @param message
     *          Text to display in the popup
     */

    public void showHealthPopup(String message) {
        this.healthPopupText = message;
        this.healthPopupCooldown = Core.getCooldown(500);
        this.healthPopupCooldown.reset();
    }

    /**
	 * Returns a GameState object representing the status of the game.
	 * 
	 * @return Current game state.
	 */
	    public final GameState getGameState() {
	        if (this.coin > 2000) {
	            AchievementManager.getInstance().unlockAchievement("Mr. Greedy");
	        }
	        return new GameState(this.level, this.score, this.lives,
	                this.bulletsShot, this.shipsDestroyed,this.coin);
	    }
	/**
	 * Adds one life to the player.
	 */
	public final void gainLife() {
		if (this.lives < this.maxLives) {
			this.lives++;
		}
	}
}
