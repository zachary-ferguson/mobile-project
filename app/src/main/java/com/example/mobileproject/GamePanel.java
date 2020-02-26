package com.example.mobileproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.ArrayList;

import static android.graphics.Color.rgb;
import static java.lang.Math.abs;


public class GamePanel extends SurfaceView implements SurfaceHolder.Callback{
    private MainThread thread;

    private RectPlayer rectPlayer;
    private CirclePlayer circlePlayer;
    private Point playerPoint;
    private ObstacleManager obstacleManager;
    private LevelManager levelManager;
    private int currentLevel = 0;

    private int startX;
    private int startY;

    private int playerWidth;
    private int playerHeight;

    private float ySpeed;
    private float xSpeed;

    private float leftCheck;
    private float rightCheck;
    private float topCheck;
    private float bottomCheck;
    private float minCollisionVertical;
    private float minCollisionHorizontal;
    private float minCollision;

    private boolean movingPlayer = false;

    private boolean death = false;
    private boolean goal = false;
    private boolean sand = false;
    private OrientationData orientationData; //move to gamePlayScene when made
    private long frameTime; //move to gamePlayScene when made
    private long startTime;
    private long endTime;
    private long endTimeMS;

    private Paint paint = new Paint();
    private Paint paintStroke = new Paint();

    public GamePanel(Context context) {
        super(context);

        getHolder().addCallback(this);

        Constants.CURRENT_CONTEXT = context;

        thread = new MainThread(getHolder(), this);

        orientationData = new OrientationData();
        orientationData.register();
        frameTime = System.currentTimeMillis();

        playerWidth = Constants.Y_UNIT/2;
        playerHeight = Constants.Y_UNIT/2;

        circlePlayer = new CirclePlayer(new ShapeDrawable(new OvalShape()), Color.WHITE,startX,startY,playerWidth,playerHeight);

        obstacleManager = new ObstacleManager();
        levelManager = new LevelManager();
        obstacleManager.createObstacles(levelManager.getLevel(currentLevel));
        playerPoint = levelManager.getLevel(currentLevel).getStart();
        circlePlayer.update(playerPoint);

        startTime = System.currentTimeMillis();

        setFocusable(true);
    }

    public void reset() {
        obstacleManager = new ObstacleManager();
        try{
            obstacleManager.createObstacles(levelManager.getLevel(currentLevel));
            playerPoint = levelManager.getLevel(currentLevel).getStart();
            circlePlayer.update(playerPoint);
            movingPlayer = false;
            startTime = System.currentTimeMillis();
        } catch(Exception e){

        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new MainThread(getHolder(), this);
        Constants.INIT_TIME = System.currentTimeMillis();

        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while(retry) { //REMEMBER to go back to episode 7 once obstacle stuff is set up
            try {
                thread.setRunning(false);
                thread.join();
            } catch(Exception e) {e.printStackTrace();}
            retry = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(!death && !goal && circlePlayer.getShape().getBounds().contains((int)event.getX(), (int)event.getY())) {
                    //System.out.println("Test tap");
                    movingPlayer = true;
                }
                if(death) {
                    reset();
                    death = false;
                    orientationData.newGame();
                }else if(goal){
                    currentLevel++;
                    reset();
                    goal = false;
                    orientationData.newGame();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(!death && !goal && movingPlayer){
                    //System.out.println("Test move");
                    playerPoint.set((int)event.getX(), (int)event.getY());
                }
                break;
            case MotionEvent.ACTION_UP:
                movingPlayer = false;
                break;
        }

        return true; //Look into this more??
        //return super.onTouchEvent(event);
    }

    public void update() {

        if(!death && !goal){
            if(frameTime < Constants.INIT_TIME)
                frameTime = Constants.INIT_TIME;
            int elapsedTime = (int)(System.currentTimeMillis() - frameTime);
            frameTime = System.currentTimeMillis();
            if(orientationData.getOrientation() != null && orientationData.getStartOrientation() != null){
                float pitch = orientationData.getOrientation()[1] - orientationData.getStartOrientation()[1];
                float roll = orientationData.getOrientation()[2] - orientationData.getStartOrientation()[2];

                if(sand){
                    ySpeed = roll * Constants.SCREEN_WIDTH/2000f;
                    xSpeed = pitch * Constants.SCREEN_HEIGHT/2000f;
                } else {
                    ySpeed = roll * Constants.SCREEN_WIDTH/1000f;
                    xSpeed = pitch * Constants.SCREEN_HEIGHT/1000f;
                }


                playerPoint.x -= abs(xSpeed*elapsedTime) > 1 ? xSpeed*elapsedTime : 0;
                playerPoint.y -= abs(ySpeed*elapsedTime) > 1 ? ySpeed*elapsedTime : 0;
            }

            if(playerPoint.x - playerWidth/2<0)
                playerPoint.x = playerWidth/2;
            else if(playerPoint.x + playerWidth/2 > Constants.SCREEN_WIDTH)
                playerPoint.x = Constants.SCREEN_WIDTH - playerWidth/2;
            if(playerPoint.y - playerHeight/2 < 0)
                playerPoint.y = playerHeight/2;
            else if(playerPoint.y + playerHeight/2 > Constants.SCREEN_HEIGHT)
                playerPoint.y = Constants.SCREEN_HEIGHT - playerHeight/2;

            circlePlayer.update(playerPoint);
            obstacleManager.update();

            ArrayList<Obstacle> collideObstacles = obstacleManager.playerCollide(circlePlayer);

            for(Obstacle collideObstacle : collideObstacles) {
                if (collideObstacle != null) {
                    if (collideObstacle.getType() == 1) {
                        //System.out.println("Player Death");
                        death = true;
                    } else if (collideObstacle.getType() == 0) {
                        Rect obRect = collideObstacle.getRectangle();

                        leftCheck = abs(playerPoint.x + playerWidth / 2 - obRect.left);
                        rightCheck = abs(playerPoint.x - playerWidth / 2 - obRect.right);
                        topCheck = abs(playerPoint.y + playerHeight / 2 - obRect.top);
                        bottomCheck = abs(playerPoint.y - playerHeight / 2 - obRect.bottom);

                        minCollisionVertical = Math.min(topCheck, bottomCheck);
                        minCollisionHorizontal = Math.min(leftCheck, rightCheck);
                        minCollision = Math.min(minCollisionHorizontal, minCollisionVertical);
                        if (minCollision == leftCheck) {
                            playerPoint.x = obRect.left - playerWidth / 2;
                        } else if (minCollision == rightCheck) {
                            playerPoint.x = obRect.right + playerWidth / 2;
                        } else if (minCollision == bottomCheck) {
                            playerPoint.y = obRect.bottom + playerHeight / 2;
                        } else {
                            playerPoint.y = obRect.top - playerHeight / 2;
                        }
                        circlePlayer.update(playerPoint);
                    } else if (collideObstacle.getType() == 2) {
                        endTimeMS = System.currentTimeMillis() - startTime;
                        endTime = endTimeMS / 1000;
                        endTimeMS -= (endTime * 1000);
                        goal = true;
                    }
                    if (collideObstacle.getType() == 3) {
                        sand = true;
                    } else {
                        sand = false;
                    }
                } else {
                    sand = false;
                }
            }
        }
    }

    @Override
    public void draw(Canvas canvas){
        super.draw(canvas);

        canvas.drawColor(rgb(26, 125, 51));

        //rectPlayer.draw(canvas);
        obstacleManager.draw(canvas);
        circlePlayer.draw(canvas);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(100);
        paint.setColor(rgb(255, 255, 255));
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setStrokeWidth(3);
        paintStroke.setColor(rgb(0,0,0));
        paintStroke.setTextAlign(Paint.Align.CENTER);
        paintStroke.setTextSize(100);

        if(death) {
            canvas.drawText("OUT OF BOUNDS - Tap To Retry",Constants.SCREEN_WIDTH/2,Constants.SCREEN_HEIGHT/2,paint);
            canvas.drawText("OUT OF BOUNDS - Tap To Retry",Constants.SCREEN_WIDTH/2,Constants.SCREEN_HEIGHT/2,paintStroke);
        } else if(goal) {
            canvas.drawText("Completed in " + endTime + "." + endTimeMS + "s - Tap For Next Hole",Constants.SCREEN_WIDTH/2,Constants.SCREEN_HEIGHT/2,paint);
            canvas.drawText("Completed in " + endTime + "." + endTimeMS + "s - Tap For Next Hole",Constants.SCREEN_WIDTH/2,Constants.SCREEN_HEIGHT/2,paintStroke);
        }
    }
}
