package com.minxing.client.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.minxing.client.AppConstants;
import com.minxing.client.R;
import com.minxing.client.activity.GesturePasswordActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NinePointLineView extends View {
	private Paint linePaint = new Paint();
	private Paint pointPaint = new Paint();
	String defaultUri = "drawable://" + R.drawable.icon_gesture_pwd_dot_off;
	private Bitmap defaultBitmap = ImageLoader.getInstance().loadImageSync(defaultUri, AppConstants.NO_CACHE_OPTIONS);
	private int defaultBitmapRadius = defaultBitmap.getWidth() / 2;
	String selectedUri = "drawable://" + R.drawable.icon_gesture_pwd_dot_on;
	private Bitmap selectedBitmap = ImageLoader.getInstance().loadImageSync(selectedUri, AppConstants.NO_CACHE_OPTIONS);
	private int selectedBitmapDiameter = selectedBitmap.getWidth();
	private int selectedBitmapRadius = selectedBitmapDiameter / 2;
	private PointInfo[] points = new PointInfo[9];
	private PointInfo startPoint = null;
	private int width, height;
	private int moveX, moveY;
	private int startX, startY;
	private boolean isUp = false;
	private GesturePasswordActivity mContext;

	private List<String> lockString = new ArrayList<String>();

	public NinePointLineView(Context context) {
		super(context);
		mContext = (GesturePasswordActivity) context;
		initPaint();
	}

	public NinePointLineView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = (GesturePasswordActivity) context;
		initPaint();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		width = getMeasuredWidth();
		height = getMeasuredHeight();
		if (width != 0 && height != 0) {
			initPoints(points);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (moveX != 0 && moveY != 0 && startX != 0 && startY != 0) {

		}
		drawNinePoint(canvas);
		super.onDraw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean flag = true;
		try {
			if (isUp) {
				finishDraw();
				flag = false;
			} else {
				handlingEvent(event);
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return flag;
	}

	private void handlingEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			moveX = (int) event.getX();
			moveY = (int) event.getY();
			for (int i = 0; i < points.length; i++) {
				PointInfo temp = points[i];
				if (temp.isInMyPlace(moveX, moveY) && temp.isSelected() == false) {
					temp.setSelected(true);
					startX = temp.getCenterX();
					startY = temp.getCenterY();
					int len = lockString.size();
					if (len != 0) {
						String preId = lockString.get(len - 1);

						points[new Integer(preId).intValue()].setNextId(temp.getId());
					}
					lockString.add(new Integer(temp.getId()).toString());
					break;
				}
			}
			invalidate(0, 0, width, height);
			break;

		case MotionEvent.ACTION_DOWN:
			int downX = (int) event.getX();
			int downY = (int) event.getY();
			for (PointInfo temp : points) {
				if (temp.isInMyPlace(downX, downY)) {
					temp.setSelected(true);
					startPoint = temp;
					startX = temp.getCenterX();
					startY = temp.getCenterY();
					lockString.add(new Integer(temp.getId()).toString());
					break;
				}
			}
			invalidate(0, 0, width, height);
			break;

		case MotionEvent.ACTION_UP:
			startX = startY = moveX = moveY = 0;
			isUp = true;
			invalidate();
			mContext.handleGesturePwd(getPwd());
			break;
		}
	}

	public void finishDraw() {
		for (PointInfo temp : points) {
			if (temp != null) {
				temp.setSelected(false);
				temp.setNextId(temp.getId());
			}
		}
		lockString.clear();
		isUp = false;
		invalidate();
	}

	private void initPoints(PointInfo[] points) {
		int len = points.length;
		int seletedSpacing = (width - selectedBitmapDiameter * 3) / 4;
		int seletedX = seletedSpacing;
		int seletedY = seletedSpacing;
		int defaultX = seletedX + selectedBitmapRadius - defaultBitmapRadius;
		int defaultY = seletedY + selectedBitmapRadius - defaultBitmapRadius;
		for (int i = 0; i < len; i++) {
			if (i == 3 || i == 6) {
				seletedX = seletedSpacing;
				seletedY += selectedBitmapDiameter + seletedSpacing;
				defaultX = seletedX + selectedBitmapRadius - defaultBitmapRadius;
				defaultY += selectedBitmapDiameter + seletedSpacing;
			}
			points[i] = new PointInfo(i, defaultX, defaultY, seletedX, seletedY);
			seletedX += selectedBitmapDiameter + seletedSpacing;
			defaultX += selectedBitmapDiameter + seletedSpacing;
		}
	}

	private void initPaint() {
		initLinePaint(linePaint);
	}

	private void initLinePaint(Paint paint) {
		paint.setColor(0x881a85ff);
		paint.setStrokeWidth(30);
		paint.setAntiAlias(true);
		paint.setStrokeCap(Cap.ROUND);
	}

	private List<String> insertPoint(int preID, int id) {
		int preIndex = lockString.indexOf(new Integer(preID).toString());
		List<String> tempList = new ArrayList<String>();
		for (int i = 0; i < lockString.size(); i++) {
			tempList.add(lockString.get(i));
			if (i == preIndex) {
				tempList.add(new Integer(id).toString());
			}
		}
		return tempList;
	}

	private boolean checkXLastAndNextSelect(int i, PointInfo[] points) {
		if (points[i] == null) {
			return false;
		}

		if (points[i].isSelected()) {
			return false;
		}
		if ((i + 1) % 3 != 2) {
			return false;
		}
		if (points[i - 1].nextId != i + 1 && points[i + 1].nextId != i - 1) {
			return false;
		}
		if (!points[i - 1].isSelected() || !points[i + 1].isSelected()) {
			return false;
		}
		int lastID = -1;
		if (points[i - 1].nextId == i + 1) {
			lastID = i - 1;
		} else {
			lastID = i + 1;
		}

		lockString = insertPoint(lastID, points[i].getId());
		points[i].setSelected(true);
		return true;

	}

	private boolean checkYLastAndNextSelect(int i, PointInfo[] points) {
		if (i < 3 || i > 5) {
			return false;
		}

		if (points[i] == null) {
			return false;
		}

		if (points[i].isSelected()) {
			return false;
		}
		if (points[i - 3].nextId != i + 3 && points[i + 3].nextId != i - 3) {
			return false;
		}
		if (points[i - 3].isSelected() && points[i + 3].isSelected()) {
			int lastID = -1;
			// int lastindex;
			if (points[i - 3].nextId == i + 3) {
				lastID = i - 3;
			} else {
				lastID = i + 3;
			}

			lockString = insertPoint(lastID, points[i].getId());
			points[i].setSelected(true);
			return true;
		}
		return false;
	}

	private boolean checkMLastAndNextSelect(int i, PointInfo[] points) {
		if (i != 4) {
			return false;
		}

		if (points[i] == null) {
			return false;
		}

		if (points[i].isSelected()) {
			return false;
		}

		if (points[i - 4].nextId != i + 4 && points[i + 4].nextId != i - 4 && points[i - 2].nextId != i + 2 && points[i + 2].nextId != i - 2) {
			return false;
		}

		int lastID = -1;
		if (points[i - 4].isSelected() && points[i + 4].isSelected()) {

			if (points[i - 4].nextId == i + 4) {
				lastID = i - 4;
			} else if (points[i + 4].nextId == i - 4) {
				lastID = i + 4;
			} else {
				return false;
			}
			lockString = insertPoint(lastID, points[i].getId());
			points[i].setSelected(true);
			return true;

		}
		if (points[i - 2].isSelected() && points[i + 2].isSelected()) {
			if (points[i - 2].nextId == i + 2) {
				lastID = i - 2;
			} else if (points[i + 2].nextId == i - 2) {
				lastID = i + 2;
			} else {
				return false;
			}
			lockString = insertPoint(lastID, points[i].getId());
			points[i].setSelected(true);
			return true;
		}

		return false;
	}

	private void drawNinePoint(Canvas canvas) {
		for (int i = 0; i < points.length; i++) {
			checkXLastAndNextSelect(i, points);
			checkYLastAndNextSelect(i, points);
			checkMLastAndNextSelect(i, points);

		}
		if (startPoint != null) {
			drawEachLine(canvas, startPoint);
		}

		for (PointInfo pointInfo : points) {
			if (pointInfo != null) {
				if (pointInfo.isSelected()) {
					canvas.drawBitmap(selectedBitmap, pointInfo.getSeletedX(), pointInfo.getSeletedY(), pointPaint);
				} else {
					canvas.drawBitmap(defaultBitmap, pointInfo.getDefaultX(), pointInfo.getDefaultY(), pointPaint);
				}
			}
		}
	}

	private void drawEachLine(Canvas canvas, PointInfo point) {
		if (point.hasNextId()) {
			int n = point.getNextId();
			drawLine(canvas, point.getCenterX(), point.getCenterY(), points[n].getCenterX(), points[n].getCenterY());
			drawEachLine(canvas, points[n]);
		}
	}

	private void drawLine(Canvas canvas, float startX, float startY, float stopX, float stopY) {
		canvas.drawLine(startX, startY, stopX, stopY, linePaint);
	}

	private class PointInfo {
		private int id;
		private int nextId;
		private boolean selected;
		private int defaultX;
		private int defaultY;
		private int seletedX;
		private int seletedY;

		public PointInfo(int id, int defaultX, int defaultY, int seletedX, int seletedY) {
			this.id = id;
			this.nextId = id;
			this.defaultX = defaultX;
			this.defaultY = defaultY;
			this.seletedX = seletedX;
			this.seletedY = seletedY;
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}

		public int getId() {
			return id;
		}

		public int getDefaultX() {
			return defaultX;
		}

		public int getDefaultY() {
			return defaultY;
		}

		public int getSeletedX() {
			return seletedX;
		}

		public int getSeletedY() {
			return seletedY;
		}

		public int getCenterX() {
			return seletedX + selectedBitmapRadius;
		}

		public int getCenterY() {
			return seletedY + selectedBitmapRadius;
		}

		public boolean hasNextId() {
			return nextId != id;
		}

		public int getNextId() {
			return nextId;
		}

		public void setNextId(int nextId) {
			this.nextId = nextId;
		}

		public boolean isInMyPlace(int x, int y) {
			boolean inX = x > seletedX && x < (seletedX + selectedBitmapDiameter);
			boolean inY = y > seletedY && y < (seletedY + selectedBitmapDiameter);
			return (inX && inY);
		}
	}

	private String getPwd() {
		StringBuffer pwdBuffer = new StringBuffer();
		for (String s : lockString) {
			pwdBuffer.append(s);
		}
		return pwdBuffer.toString();
	}
}
