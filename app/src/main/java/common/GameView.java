package common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import com.example.z.moveboxplus.GameActivity;
import com.example.z.moveboxplus.R;

/**
 * Created by Administrator on 2016/8/25/025.
 */
public class GameView extends View {
    private float mCellWidth;
    public static final int CELL_NUM_PER_LINE = 12;
    private Bitmap manBitmap = null;
    private Bitmap boxBitmap = null;
    private Bitmap flagBitmap = null;
    private Bitmap wallBitmap = null;
    private Bitmap passBitmap = null;

    private int mManRow = 0;
    private int mManColumn = 0;

    private int viewWidth,viewHeight;

    private int no = 0;
    private int box = 1;
    private int man = 2;
    private int flag = 3;
    private int wall = 4;
    private int flag_man = 5;
    private int flag_box = 6;

    private GameActivity gameActivity;
    private SoundPool soundPool;
    private int step;
    private int[][] labelInCells = null;


    public GameView(Context context) {
        super(context);
        gameActivity = (GameActivity)context;
        soundPool = new SoundPool(10,AudioManager.STREAM_SYSTEM,5);
        step = soundPool.load(gameActivity,R.raw.step,1);
        manBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.eggman);
        boxBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.box);
        wallBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.wall);
        flagBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.flag);
        passBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pass);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gameActivity = (GameActivity)context;
        soundPool = new SoundPool(10,AudioManager.STREAM_SYSTEM,5);
        step = soundPool.load(gameActivity,R.raw.step,1);
        manBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.eggman);
        boxBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.box);
        wallBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.wall);
        flagBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.flag);
        passBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pass);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCellWidth = w / CELL_NUM_PER_LINE;
        viewWidth = w;
        viewHeight = h;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //背景色
        Paint background = new Paint();
        background.setColor(getResources().getColor(R.color.background));
        canvas.drawRect(0, 0, getWidth(), getHeight(), background);

        //绘制游戏区域
        Paint linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        for (int r = 0; r <= CELL_NUM_PER_LINE; r++)
            canvas.drawLine(0, r * mCellWidth, getWidth(), r * mCellWidth, linePaint);
        for (int c = 0; c <= CELL_NUM_PER_LINE; c++)
            canvas.drawLine(c * mCellWidth, 0, c * mCellWidth, CELL_NUM_PER_LINE * mCellWidth, linePaint);
        drawGameBoard(canvas);
    }

    private void drawGameBoard(Canvas canvas) {
        Rect srcRect;       //srcRect用来记住图片的绘制区域
        Rect destRect;      //destRect用来记住游戏区域的单元格所占据的矩形区域
        labelInCells = gameActivity.getCurrentLevel();
        for (int r = 0; r < labelInCells.length; r++)
            for (int c = 0; c < labelInCells[r].length; c++) {
                destRect = getRect(r, c);
                switch (labelInCells[r][c]) {
                    case 4:
                        srcRect = new Rect(0, 0, wallBitmap.getWidth(), wallBitmap.getHeight());
                        canvas.drawBitmap(wallBitmap, srcRect, destRect, null);
                        break;
                    case 1:
                        srcRect = new Rect(0, 0, boxBitmap.getWidth(), boxBitmap.getHeight());
                        canvas.drawBitmap(boxBitmap, srcRect, destRect, null);
                        break;
                    case 3:
                        srcRect = new Rect(0, 0, flagBitmap.getWidth(), flagBitmap.getHeight());
                        canvas.drawBitmap(flagBitmap, srcRect, destRect, null);
                        break;
                    case 2:
                        srcRect = new Rect(0, 0, manBitmap.getWidth(), manBitmap.getHeight());
                        mManRow = r;
                        mManColumn = c;
                        canvas.drawBitmap(manBitmap, srcRect, destRect, null);
                        break;
                    case 5:
                        srcRect = new Rect(0, 0, manBitmap.getWidth(), manBitmap.getHeight());
                        canvas.drawBitmap(manBitmap, srcRect, destRect, null);
                        break;
                    case 6:
                        srcRect = new Rect(0, 0, boxBitmap.getWidth(), boxBitmap.getHeight());
                        canvas.drawBitmap(boxBitmap, srcRect, destRect, null);
                        break;
                }
            }
        if (isPass()) {
            int cHeight = canvas.getHeight();
            int width = passBitmap.getWidth();
            int height = passBitmap.getHeight();
            Rect srcRect2 = new Rect(0, 0, passBitmap.getWidth(), passBitmap.getHeight());
            Rect destRect2 = new Rect((viewWidth-passBitmap.getWidth())/2,(viewHeight-passBitmap.getHeight())/2,(viewWidth+passBitmap.getWidth())/2,(viewHeight+passBitmap.getHeight())/2);
            canvas.drawBitmap(passBitmap, srcRect2, destRect2, null);
            gameActivity.setLevelState();
        }


    }

    //触摸反应
    public boolean onTouchEvent(MotionEvent event) {
        if(isPass())
            return false;
        if (event.getAction() != MotionEvent.ACTION_DOWN  ) {
            return true;
        }
        int touch_x = (int) event.getX();
        int touch_y = (int) event.getY();
        //向下走
        if (touch_blow_to_man(touch_x, touch_y, mManRow, mManColumn)) {
            if (isFlagUnderBox_blow() & labelInCells[mManRow + 2][mManColumn] != wall & labelInCells[mManRow + 2][mManColumn] != box & labelInCells[mManRow + 2][mManColumn] != flag_box) {
                
                if (labelInCells[mManRow + 2][mManColumn] == flag) {
                    labelInCells[mManRow + 2][mManColumn] = flag_box;
                } else {
                    labelInCells[mManRow + 2][mManColumn] = box;
                }
                labelInCells[mManRow + 1][mManColumn] = flag_man;
                if (isFlagUnderMan()) {
                    labelInCells[mManRow][mManColumn] = flag;
                } else {
                    labelInCells[mManRow][mManColumn] = no;
                }
                mManRow++;
                
            } else {
                //人下有箱子，箱子下有旗帜
                if (isBoxBlowToMan() & isFlagBlowToBox()) {
                    
                    labelInCells[mManRow + 2][mManColumn] = flag_box;
                    if (isFlagUnderBox_blow()) {
                        labelInCells[mManRow + 1][mManColumn] = flag_man;
                    } else {
                        labelInCells[mManRow + 1][mManColumn] = man;
                    }
                    if (isFlagUnderMan()) {
                        labelInCells[mManRow][mManColumn] = flag;
                    } else {
                        labelInCells[mManRow][mManColumn] = no;
                    }
                    mManRow++;
                    
                } else {
                    //人下有箱子，箱子下为空
                    if (isBoxBlowToMan() & isNullBlowToBox()) {
                        
                        labelInCells[mManRow + 2][mManColumn] = box;
                        if (isFlagUnderBox_blow()) {
                            labelInCells[mManRow + 1][mManColumn] = flag_man;
                        } else {
                            labelInCells[mManRow + 1][mManColumn] = man;
                        }
                        if (isFlagUnderMan()) {
                            labelInCells[mManRow][mManColumn] = flag;
                        } else {
                            labelInCells[mManRow][mManColumn] = no;
                        }
                        mManRow++;
                        
                    } else {
                        //人下为旗帜
                        if (isFlagBlowToMan()) {
                            
                            labelInCells[mManRow + 1][mManColumn] = flag_man;
                            if (isFlagUnderMan()) {
                                labelInCells[mManRow][mManColumn] = flag;
                            } else {
                                labelInCells[mManRow][mManColumn] = no;
                            }
                            mManRow++;
                            
                        } else {
                            //人的下面为空
                            if (isNullBlowToMan()) {
                                
                                labelInCells[mManRow + 1][mManColumn] = man;
                                if (isFlagUnderMan()) {
                                    labelInCells[mManRow][mManColumn] = flag;
                                } else {
                                    labelInCells[mManRow][mManColumn] = no;
                                }
                                mManRow++;
                                
                            }
                        }
                    }
                }
            }
        }

        //向上走
        if (touch_up_to_man(touch_x, touch_y, mManRow, mManColumn)) {
            if (isFlagUnderBox_up() & labelInCells[mManRow - 2][mManColumn] != wall & labelInCells[mManRow - 2][mManColumn] != box & labelInCells[mManRow - 2][mManColumn] != flag_box) {
                
                if (labelInCells[mManRow - 2][mManColumn] == flag) {
                    labelInCells[mManRow - 2][mManColumn] = flag_box;
                } else {
                    labelInCells[mManRow - 2][mManColumn] = box;
                }
                labelInCells[mManRow - 1][mManColumn] = flag_man;
                if (isFlagUnderMan()) {
                    labelInCells[mManRow][mManColumn] = flag;
                } else {
                    labelInCells[mManRow][mManColumn] = no;
                }
                mManRow--;
                
            } else {
                //人上有箱子，箱子上有旗帜
                if (isBoxUpToMan() & isFlagUpToBox()) {
                    
                    labelInCells[mManRow - 2][mManColumn] = flag_box;
                    if (isFlagUnderBox_up()) {
                        labelInCells[mManRow - 1][mManColumn] = flag_man;
                    } else {
                        labelInCells[mManRow - 1][mManColumn] = man;
                    }
                    if (isFlagUnderMan()) {
                        labelInCells[mManRow][mManColumn] = flag;
                    } else {
                        labelInCells[mManRow][mManColumn] = no;
                    }
                    mManRow--;
                    
                } else {
                    //人上有箱子，箱子上为空
                    if (isBoxUpToMan() & isNullUpToBox()) {
                        
                        labelInCells[mManRow - 2][mManColumn] = box;
                        if (isFlagUnderBox_up()) {
                            labelInCells[mManRow - 1][mManColumn] = flag_man;
                        } else {
                            labelInCells[mManRow - 1][mManColumn] = man;
                        }
                        if (isFlagUnderMan()) {
                            labelInCells[mManRow][mManColumn] = flag;
                        } else {
                            labelInCells[mManRow][mManColumn] = no;
                        }
                        mManRow--;
                        
                    } else {
                        //人上为旗帜
                        if (isFlagUpToMan()) {
                            
                            labelInCells[mManRow - 1][mManColumn] = flag_man;
                            if (isFlagUnderMan()) {
                                labelInCells[mManRow][mManColumn] = flag;
                            } else {
                                labelInCells[mManRow][mManColumn] = no;
                            }
                            mManRow--;
                            
                        } else {
                            //人的上面为空
                            if (isNullUpToMan()) {
                                
                                labelInCells[mManRow - 1][mManColumn] = man;
                                if (isFlagUnderMan()) {
                                    labelInCells[mManRow][mManColumn] = flag;
                                } else {
                                    labelInCells[mManRow][mManColumn] = no;
                                }
                                mManRow--;
                                
                            }
                        }
                    }
                }
            }
        }

        //向右走
        if (touch_right_to_man(touch_x, touch_y, mManRow, mManColumn)) {
            if (isFlagUnderBox_right() & labelInCells[mManRow][mManColumn + 2] != wall & labelInCells[mManRow][mManColumn + 2] != box & labelInCells[mManRow][mManColumn + 2] != flag_box) {
                
                if (labelInCells[mManRow][mManColumn + 2] == flag) {
                    labelInCells[mManRow][mManColumn + 2] = flag_box;
                } else {
                    labelInCells[mManRow][mManColumn + 2] = box;
                }
                labelInCells[mManRow][mManColumn + 1] = flag_man;
                if (isFlagUnderMan()) {
                    labelInCells[mManRow][mManColumn] = flag;
                } else {
                    labelInCells[mManRow][mManColumn] = no;
                }
                mManColumn++;
                
            } else {
                //人右有箱子，箱子右有旗帜
                if (isBoxRightToMan() & isFlagRightToBox()) {
                    
                    labelInCells[mManRow][mManColumn + 2] = flag_box;
                    if (isFlagUnderBox_right()) {
                        labelInCells[mManRow][mManColumn + 1] = flag_man;
                    } else {
                        labelInCells[mManRow][mManColumn + 1] = man;
                    }
                    if (isFlagUnderMan()) {
                        labelInCells[mManRow][mManColumn] = flag;
                    } else {
                        labelInCells[mManRow][mManColumn] = no;
                    }
                    mManColumn++;
                    
                } else {
                    //人右有箱子，箱子右为空
                    if (isBoxRightToMan() & isNullRightToBox()) {
                        
                        labelInCells[mManRow][mManColumn + 2] = box;
                        if (isFlagUnderBox_right()) {
                            labelInCells[mManRow][mManColumn + 1] = flag_man;
                        } else {
                            labelInCells[mManRow][mManColumn + 1] = man;
                        }
                        if (isFlagUnderMan()) {
                            labelInCells[mManRow][mManColumn] = flag;
                        } else {
                            labelInCells[mManRow][mManColumn] = no;
                        }
                        mManColumn++;
                        
                    } else {
                        //人右为旗帜
                        if (isFlagRightToMan()) {
                            
                            labelInCells[mManRow][mManColumn + 1] = flag_man;
                            if (isFlagUnderMan()) {
                                labelInCells[mManRow][mManColumn] = flag;
                            } else {
                                labelInCells[mManRow][mManColumn] = no;
                            }
                            mManColumn++;
                            
                        } else {
                            //人的右面为空
                            if (isNullRightToMan()) {
                                
                                labelInCells[mManRow][mManColumn + 1] = man;
                                if (isFlagUnderMan()) {
                                    labelInCells[mManRow][mManColumn] = flag;
                                } else {
                                    labelInCells[mManRow][mManColumn] = no;
                                }
                                mManColumn++;
                                
                            }
                        }
                    }
                }
            }
        }


        //向左走
        if (touch_left_to_man(touch_x, touch_y, mManRow, mManColumn)) {
            if (isFlagUnderBox_left() & labelInCells[mManRow][mManColumn - 2] != wall & labelInCells[mManRow][mManColumn - 2] != box & labelInCells[mManRow][mManColumn - 2] != flag_box) {
                
                if (labelInCells[mManRow][mManColumn - 2] == flag) {
                    labelInCells[mManRow][mManColumn - 2] = flag_box;
                } else {
                    labelInCells[mManRow][mManColumn - 2] = box;
                }
                labelInCells[mManRow][mManColumn - 1] = flag_man;
                if (isFlagUnderMan()) {
                    labelInCells[mManRow][mManColumn] = flag;
                } else {
                    labelInCells[mManRow][mManColumn] = no;
                }
                mManColumn--;
                
            } else {
                //人左有箱子，箱子左有旗帜
                if (isBoxLeftToMan() & isFlagLeftToBox()) {
                    
                    labelInCells[mManRow][mManColumn - 2] = flag_box;
                    if (isFlagUnderBox_left()) {
                        labelInCells[mManRow][mManColumn - 1] = flag_man;
                    } else {
                        labelInCells[mManRow][mManColumn - 1] = man;
                    }
                    if (isFlagUnderMan()) {
                        labelInCells[mManRow][mManColumn] = flag;
                    } else {
                        labelInCells[mManRow][mManColumn] = no;
                    }
                    mManColumn--;
                    
                } else {
                    //人左有箱子，箱子左为空
                    if (isBoxLeftToMan() & isNullLeftToBox()) {
                        
                        labelInCells[mManRow][mManColumn - 2] = box;
                        if (isFlagUnderBox_left()) {
                            labelInCells[mManRow][mManColumn - 1] = flag_man;
                        } else {
                            labelInCells[mManRow][mManColumn - 1] = man;
                        }
                        if (isFlagUnderMan()) {
                            labelInCells[mManRow][mManColumn] = flag;
                        } else {
                            labelInCells[mManRow][mManColumn] = no;
                        }
                        mManColumn--;
                        
                    } else {
                        //人左为旗帜
                        if (isFlagLeftToMan()) {
                            
                            labelInCells[mManRow][mManColumn - 1] = flag_man;
                            if (isFlagUnderMan()) {
                                labelInCells[mManRow][mManColumn] = flag;
                            } else {
                                labelInCells[mManRow][mManColumn] = no;
                            }
                            mManColumn--;
                            
                        } else {
                            //人的左面为空
                            if (isNullLeftToMan()) {
                                
                                labelInCells[mManRow][mManColumn - 1] = man;
                                if (isFlagUnderMan()) {
                                    labelInCells[mManRow][mManColumn] = flag;
                                } else {
                                    labelInCells[mManRow][mManColumn] = no;
                                }
                                mManColumn--;
                                
                            }
                        }
                    }
                }
            }
        }
        if(Constant.SOUND_BOOLEAN){
            soundPool.play(step,1,1,0,0,1);
        }
        postInvalidate();      //postInvalidate方法的作用是要求使游戏界面（即GameView视图）失效
        return true;
    }


    //向下行走
    private boolean touch_blow_to_man(int touch_x, int touch_y, int manRow, int manColumn) {
        int belowRow = manRow + 1;
        Rect belowRect = getRect(belowRow, manColumn);
        return belowRect.contains(touch_x, touch_y);
    }

    //向上行走
    private boolean touch_up_to_man(int touch_x, int touch_y, int manRow, int manColumn) {
        int upRow = manRow - 1;
        Rect upRect = getRect(upRow, manColumn);
        return upRect.contains(touch_x, touch_y);
    }

    //向右行走
    private boolean touch_right_to_man(int touch_x, int touch_y, int manRow, int manColumn) {
        int rightColumn = manColumn + 1;
        Rect rightRect = getRect(manRow, rightColumn);
        return rightRect.contains(touch_x, touch_y);
    }

    //向左行走
    private boolean touch_left_to_man(int touch_x, int touch_y, int manRow, int manColumn) {
        int leftColumn = manColumn - 1;
        Rect leftRect = getRect(manRow, leftColumn);
        return leftRect.contains(touch_x, touch_y);
    }



    //判断箱子是不是在人的附近
    private boolean isBoxBlowToMan() {
        return labelInCells[mManRow + 1][mManColumn] == box;
    }
    private boolean isBoxUpToMan() {
        return labelInCells[mManRow - 1][mManColumn] == box;
    }
    private boolean isBoxLeftToMan() {
        return labelInCells[mManRow][mManColumn - 1] == box;
    }
    private boolean isBoxRightToMan() {
        return labelInCells[mManRow][mManColumn + 1] == box;
    }


    //判断旗帜是不是在人的附近
    private boolean isFlagBlowToMan() {
        return labelInCells[mManRow + 1][mManColumn] == flag;
    }
    private boolean isFlagUpToMan() {
        return labelInCells[mManRow - 1][mManColumn] == flag;
    }
    private boolean isFlagLeftToMan() {
        return labelInCells[mManRow][mManColumn - 1] == flag;
    }
    private boolean isFlagRightToMan() {
        return labelInCells[mManRow][mManColumn + 1] == flag;
    }


    //判断旗帜是不是在箱子的附近
    private boolean isFlagBlowToBox() {
        return labelInCells[mManRow + 1][mManColumn] == box && labelInCells[mManRow + 2][mManColumn] == flag;
    }
    private boolean isFlagUpToBox() {
        return labelInCells[mManRow - 1][mManColumn] == box && labelInCells[mManRow - 2][mManColumn] == flag;
    }
    private boolean isFlagLeftToBox() {
        return labelInCells[mManRow][mManColumn - 1] == box && labelInCells[mManRow][mManColumn - 2] == flag;
    }
    private boolean isFlagRightToBox() {
        return labelInCells[mManRow][mManColumn + 1] == box && labelInCells[mManRow][mManColumn + 2] == flag;
    }


    //判断人附近是否为空
    private boolean isNullBlowToMan() {
        return labelInCells[mManRow + 1][mManColumn] == no;
    }
    private boolean isNullUpToMan() {
        return labelInCells[mManRow - 1][mManColumn] == no;
    }
    private boolean isNullLeftToMan() {
        return labelInCells[mManRow][mManColumn - 1] == no;
    }
    private boolean isNullRightToMan() {
        return labelInCells[mManRow][mManColumn + 1] == no;
    }


    //判断箱子附近是否为空
    private boolean isNullBlowToBox() {
        return labelInCells[mManRow + 1][mManColumn] == box && labelInCells[mManRow + 2][mManColumn] == no;
    }
    private boolean isNullUpToBox() {
        return labelInCells[mManRow - 1][mManColumn] == box && labelInCells[mManRow - 2][mManColumn] == no;
    }
    private boolean isNullLeftToBox() {
        return labelInCells[mManRow][mManColumn - 1] == box && labelInCells[mManRow][mManColumn - 2] == no;
    }
    private boolean isNullRightToBox() {
        return labelInCells[mManRow][mManColumn + 1] == box && labelInCells[mManRow][mManColumn + 2] == no;
    }


    //判断旗帜是否在人或箱子下面
    private boolean isFlagUnderMan() {
        return labelInCells[mManRow][mManColumn] == flag_man;
    }
    private boolean isFlagUnderBox_blow() {
        return labelInCells[mManRow + 1][mManColumn] == flag_box;
    }
    private boolean isFlagUnderBox_up() {
        return labelInCells[mManRow - 1][mManColumn] == flag_box;
    }
    private boolean isFlagUnderBox_left() {
        return labelInCells[mManRow][mManColumn - 1] == flag_box;
    }
    private boolean isFlagUnderBox_right() {
        return labelInCells[mManRow][mManColumn + 1] == flag_box;
    }

    //获得单元格区域
    private Rect getRect(int row, int column) {
        int left = (int) (column * mCellWidth);
        int top = (int) (row * mCellWidth);
        int right = (int) ((column + 1) * mCellWidth);
        int bottom = (int) ((row + 1) * mCellWidth);
        return new Rect(left, top, right, bottom);
    }

    private boolean isPass() {
        for (int r = 0; r < labelInCells.length; r++)
            for (int c = 0; c < labelInCells[r].length; c++) {
                if (labelInCells[r][c] == box)
                    return false;
            }
        return true;
    }

}