package com.example.mobileproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private float maxXSpeed;
    private float maxYSpeed;
    private float xAccel;
    private float yAccel;

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
    private long endTimeTotalMS;
    private long endTime;
    private long endTimeMS;
    private long currentTime;
    private long currentTimeDS;

    private Paint paint = new Paint();
    private Paint paintStroke = new Paint();
    private BitmapFactory bf;
    private Bitmap retryImage;
    private Bitmap nextImage;
    private Bitmap selectImage;
    private Bitmap starImage;
    private Bitmap starGoldImage;
    private Rect retryRect;
    private Rect nextRect;
    private Rect selectRect;

    private int starRank;

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

        ySpeed = 0;
        xSpeed = 0;

        maxYSpeed = 1.5f;
        maxXSpeed = 1.5f;

        startTime = System.currentTimeMillis();

        bf = new BitmapFactory();
        retryImage = bf.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.retry);
        retryRect = new Rect(Constants.SCREEN_WIDTH/2 - 100,(Constants.SCREEN_HEIGHT/3)*2 - 100,Constants.SCREEN_WIDTH/2 + 100,(Constants.SCREEN_HEIGHT/3)*2 + 300);
        nextImage = bf.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.next);
        nextRect = new Rect((Constants.SCREEN_WIDTH/5)*4 - 100,(Constants.SCREEN_HEIGHT/3)*2 - 100,(Constants.SCREEN_WIDTH/5)*4 + 100,(Constants.SCREEN_HEIGHT/3)*2 + 300);
        selectImage = bf.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.select);
        selectRect = new Rect(Constants.SCREEN_WIDTH/5 - 100,(Constants.SCREEN_HEIGHT/3)*2 - 100,Constants.SCREEN_WIDTH/5 + 100,(Constants.SCREEN_HEIGHT/3)*2 + 300);

        starImage = bf.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.star);
        starGoldImage = bf.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.star_gold);

        setFocusable(true);
    }

    public void reset() {
        obstacleManager = new ObstacleManager();
        try{
            obstacleManager.createObstacles(levelManager.getLevel(currentLevel));
            playerPoint = levelManager.getLevel(currentLevel).getStart();
            circlePlayer.update(playerPoint);
            movingPlayer = false;
            ySpeed = 0;
            xSpeed = 0;
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
                /**
                if(!death && !goal && circlePlayer.getShape().getBounds().contains((int)event.getX(), (int)event.getY())) {
                    //System.out.println("Test tap");
                    movingPlayer = true;
                }*/
                if(death) {
                    reset();
                    death = false;
                    orientationData.newGame();
                }else if(goal){
                    if(retryRect.contains((int)event.getX(),(int)event.getY())){
                        reset();
                        goal = false;
                    } else if(nextRect.contains((int)event.getX(),(int)event.getY())){
                        currentLevel++;
                        reset();
                        goal = false;
                    }
                }
                orientationData.newGame();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
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

                /**if(sand){
                    ySpeed = roll * Constants.SCREEN_WIDTH/2000f;
                    xSpeed = pitch * Constants.SCREEN_HEIGHT/2000f;
                } else {
                    ySpeed = roll * Constants.SCREEN_WIDTH/1000f;
                    xSpeed = pitch * Constants.SCREEN_HEIGHT/1000f;
                }*/
                xAccel = pitch * Constants.SCREEN_HEIGHT/10000f;
                yAccel = roll * Constants.SCREEN_WIDTH/15000f;
                if(sand){
                    System.out.println("Ran into sand");
                    xSpeed += xAccel/2;
                    if(xSpeed > maxXSpeed/3){
                        xSpeed = maxXSpeed/3;
                    } else if (xSpeed < -maxXSpeed/3){
                        xSpeed = -maxXSpeed/3;
                    }
                    ySpeed += yAccel/2;
                    if(ySpeed > maxYSpeed/3){
                        ySpeed = maxYSpeed/3;
                    } else if (ySpeed < -maxYSpeed/3){
                        ySpeed = -maxYSpeed/3;
                    }
                } else {
                    xSpeed += xAccel;
                    if(xSpeed > maxXSpeed){
                        xSpeed = maxXSpeed;
                    } else if (xSpeed < -maxXSpeed){
                        xSpeed = -maxXSpeed;
                    }

                    ySpeed += yAccel;
                    if(ySpeed > maxYSpeed){
                        ySpeed = maxYSpeed;
                    } else if (ySpeed < -maxYSpeed){
                        ySpeed = -maxYSpeed;
                    }
                }

                System.out.println("xSpeed: " + xSpeed + " ySpeed: " + ySpeed);

                playerPoint.x -= abs(xSpeed*elapsedTime) > 2 ? xSpeed*elapsedTime : 0;
                playerPoint.y -= abs(ySpeed*elapsedTime) > 2 ? ySpeed*elapsedTime : 0;
            }

            if(playerPoint.x - playerWidth/2<0) {
                playerPoint.x = playerWidth / 2;
                xSpeed = -xSpeed/2;
            } else if(playerPoint.x + playerWidth/2 > Constants.SCREEN_WIDTH) {
                playerPoint.x = Constants.SCREEN_WIDTH - playerWidth / 2;
                xSpeed = -xSpeed/2;
            } if(playerPoint.y - playerHeight/2 < 0) {
                playerPoint.y = playerHeight / 2;
                ySpeed = -ySpeed/2;
            } else if(playerPoint.y + playerHeight/2 > Constants.SCREEN_HEIGHT) {
                playerPoint.y = Constants.SCREEN_HEIGHT - playerHeight / 2;
                ySpeed = -ySpeed/2;
            }
            circlePlayer.update(playerPoint);
            obstacleManager.update();

            ArrayList<Obstacle> collideObstacles = obstacleManager.playerCollide(circlePlayer);
            sand = false;
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
                            xSpeed = -xSpeed/2;
                            playerPoint.x = obRect.left - playerWidth / 2;
                        } else if (minCollision == rightCheck) {
                            xSpeed = -xSpeed/2;
                            playerPoint.x = obRect.right + playerWidth / 2;
                        } else if (minCollision == bottomCheck) {
                            playerPoint.y = obRect.bottom + playerHeight / 2;
                            ySpeed = -ySpeed/2;
                        } else {
                            playerPoint.y = obRect.top - playerHeight / 2;
                            ySpeed = -ySpeed/2;
                        }
                        circlePlayer.update(playerPoint);
                    } else if (collideObstacle.getType() == 2) {
                        endTimeTotalMS = System.currentTimeMillis() - startTime;
                        endTime = endTimeTotalMS / 1000;
                        endTimeMS = endTimeTotalMS - (endTime * 1000);
                        starRank = levelManager.getLevel(currentLevel).getStarRank(endTimeTotalMS);
                        goal = true;
                    }
                    if (collideObstacle.getType() == 3) {
                        sand = true;
                    }
                }
            }
        }
    }

    @Override
    public void draw(Canvas canvas){
        super.draw(canvas);

        canvas.drawColor(rgb(26, 125, 51));

        //rectPlayer.draw(canvas);
        if(!goal) {
            obstacleManager.draw(canvas);
            circlePlayer.draw(canvas);
        }

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(100);
        paint.setColor(rgb(255, 255, 255));
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setStrokeWidth(3);
        paintStroke.setColor(rgb(0,0,0));
        paintStroke.setTextAlign(Paint.Align.CENTER);
        paintStroke.setTextSize(100);

        if(!death && !goal) {
            currentTimeDS = System.currentTimeMillis() - startTime;
            currentTime = currentTimeDS / 1000;
            currentTimeDS -= (currentTime * 1000);
            currentTimeDS = currentTimeDS / 100;
        }

        if(!goal) {
            canvas.drawText(currentTime + "." + currentTimeDS + "s", Constants.SCREEN_WIDTH - 150, 100, paint);
            canvas.drawText(currentTime + "." + currentTimeDS + "s", Constants.SCREEN_WIDTH - 150, 100, paintStroke);
        }

        if(death) {
            canvas.drawText("OUT OF BOUNDS - Tap To Retry",Constants.SCREEN_WIDTH/2,Constants.SCREEN_HEIGHT/2,paint);
            canvas.drawText("OUT OF BOUNDS - Tap To Retry",Constants.SCREEN_WIDTH/2,Constants.SCREEN_HEIGHT/2,paintStroke);
        } else if(goal) {
            canvas.drawText("Completed in " + endTime + "." + endTimeMS + "s",Constants.SCREEN_WIDTH/2,Constants.SCREEN_HEIGHT/7,paint);
            canvas.drawBitmap(retryImage, null, new Rect(Constants.SCREEN_WIDTH/2 - 100,(Constants.SCREEN_HEIGHT/3)*2 - 100,Constants.SCREEN_WIDTH/2 + 100,(Constants.SCREEN_HEIGHT/3)*2 + 100), new Paint());
            canvas.drawText("Retry Level",Constants.SCREEN_WIDTH/2,(Constants.SCREEN_HEIGHT/3)*2 + 250,paint);
            canvas.drawBitmap(nextImage, null, new Rect((Constants.SCREEN_WIDTH/5)*4 - 100,(Constants.SCREEN_HEIGHT/3)*2 - 100,(Constants.SCREEN_WIDTH/5)*4 + 100,(Constants.SCREEN_HEIGHT/3)*2 + 100), new Paint());
            canvas.drawText("Next Level",(Constants.SCREEN_WIDTH/5)*4,(Constants.SCREEN_HEIGHT/3)*2 + 250,paint);
            canvas.drawBitmap(selectImage, null, new Rect(Constants.SCREEN_WIDTH/5 - 100,(Constants.SCREEN_HEIGHT/3)*2 - 100,Constants.SCREEN_WIDTH/5 + 100,(Constants.SCREEN_HEIGHT/3)*2 + 100), new Paint());
            canvas.drawText("Level Select",Constants.SCREEN_WIDTH/5,(Constants.SCREEN_HEIGHT/3)*2 + 250,paint);

            if(starRank == 3){
                canvas.drawText("EAGLE!",Constants.SCREEN_WIDTH/2,Constants.SCREEN_HEIGHT/4,paint);
                canvas.drawBitmap(starGoldImage, null, new Rect((Constants.SCREEN_WIDTH/5)*2 - 100,(Constants.SCREEN_HEIGHT/5)*2 - 100,(Constants.SCREEN_WIDTH/5)*2 + 100,(Constants.SCREEN_HEIGHT/5)*2 + 100), new Paint());
                canvas.drawBitmap(starGoldImage, null, new Rect(Constants.SCREEN_WIDTH/2 - 100,(Constants.SCREEN_HEIGHT/5)*2 - 100,Constants.SCREEN_WIDTH/2 + 100,(Constants.SCREEN_HEIGHT/5)*2 + 100), new Paint());
                canvas.drawBitmap(starGoldImage, null, new Rect((Constants.SCREEN_WIDTH/5)*3 - 100,(Constants.SCREEN_HEIGHT/5)*2 - 100,(Constants.SCREEN_WIDTH/5)*3 + 100,(Constants.SCREEN_HEIGHT/5)*2 + 100), new Paint());
            } else if (starRank == 2){
                canvas.drawText("BIRDIE!",Constants.SCREEN_WIDTH/2,Constants.SCREEN_HEIGHT/4,paint);
                canvas.drawBitmap(starGoldImage, null, new Rect((Constants.SCREEN_WIDTH/5)*2 - 100,(Constants.SCREEN_HEIGHT/5)*2 - 100,(Constants.SCREEN_WIDTH/5)*2 + 100,(Constants.SCREEN_HEIGHT/5)*2 + 100), new Paint());
                canvas.drawBitmap(starGoldImage, null, new Rect(Constants.SCREEN_WIDTH/2 - 100,(Constants.SCREEN_HEIGHT/5)*2 - 100,Constants.SCREEN_WIDTH/2 + 100,(Constants.SCREEN_HEIGHT/5)*2 + 100), new Paint());
                canvas.drawBitmap(starImage, null, new Rect((Constants.SCREEN_WIDTH/5)*3 - 100,(Constants.SCREEN_HEIGHT/5)*2 - 100,(Constants.SCREEN_WIDTH/5)*3 + 100,(Constants.SCREEN_HEIGHT/5)*2 + 100), new Paint());
            } else {
                canvas.drawText("PAR!",Constants.SCREEN_WIDTH/2,Constants.SCREEN_HEIGHT/4,paint);
                canvas.drawBitmap(starGoldImage, null, new Rect((Constants.SCREEN_WIDTH/5)*2 - 100,(Constants.SCREEN_HEIGHT/5)*2 - 100,(Constants.SCREEN_WIDTH/5)*2 + 100,(Constants.SCREEN_HEIGHT/5)*2 + 100), new Paint());
                canvas.drawBitmap(starImage, null, new Rect(Constants.SCREEN_WIDTH/2 - 100,(Constants.SCREEN_HEIGHT/5)*2 - 100,Constants.SCREEN_WIDTH/2 + 100,(Constants.SCREEN_HEIGHT/5)*2 + 100), new Paint());
                canvas.drawBitmap(starImage, null, new Rect((Constants.SCREEN_WIDTH/5)*3 - 100,(Constants.SCREEN_HEIGHT/5)*2 - 100,(Constants.SCREEN_WIDTH/5)*3 + 100,(Constants.SCREEN_HEIGHT/5)*2 + 100), new Paint());
            }
        }
    }
}
