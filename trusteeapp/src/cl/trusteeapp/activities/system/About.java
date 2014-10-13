/**
 * 
 */
package cl.trusteeapp.activities.system;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ScrollView;
import android.widget.ViewFlipper;
import cl.trusteeapp.R;
import cl.trusteeapp.activities.ABaseActivity;

/**
 * @author cursor
 * 
 */
public class About extends ABaseActivity implements OnTouchListener {

	private ViewFlipper viewFlipper;
	private ScrollView scrollView;
	private float mx;

	protected final GestureDetector gestureDetector = new GestureDetector(new GestureListener());

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
		viewFlipper.setInAnimation(this, android.R.anim.fade_in);
		viewFlipper.setOutAnimation(this, android.R.anim.fade_out);
		if (savedInstanceState != null) {
			int flipperPosition = savedInstanceState.getInt("TAB_NUMBER");
			viewFlipper.setDisplayedChild(flipperPosition);
		}
		scrollView = (ScrollView) findViewById(R.id.scrollView);
		viewFlipper.setOnTouchListener(this);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		int position = viewFlipper.getDisplayedChild();
		savedInstanceState.putInt("TAB_NUMBER", position);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		float curX;

		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:
			mx = event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			curX = event.getX();
			final float xxM = curX;
			scrollView.post(new Runnable() {
			    @Override
			    public void run() {
			    	scrollView.scrollBy((int) (mx - xxM), 0);
			    }
			});
			mx = curX;
			break;
		case MotionEvent.ACTION_UP:
			curX = event.getX();
			final float xx = curX;
			scrollView.post(new Runnable() {
			    @Override
			    public void run() {
			    	scrollView.scrollTo((int) (mx - xx), 0);
			    } 
			});
			mx = curX;
			break;
		}

		return gestureDetector.onTouchEvent(event);
	}

	private final class GestureListener extends SimpleOnGestureListener {

		private static final int SWIPE_THRESHOLD = 100;
		private static final int SWIPE_VELOCITY_THRESHOLD = 100;

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			boolean result = false;
			try {
				float diffY = e2.getY() - e1.getY();
				float diffX = e2.getX() - e1.getX();
				if (Math.abs(diffX) > Math.abs(diffY)) {
					if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
						if (diffX > 0) {
							onSwipeRight();
						} else {
							onSwipeLeft();
						}
					}
				} else {
					if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
						if (diffY > 0) {
							onSwipeBottom();
						} else {
							onSwipeTop();
						}
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			return result;
		}

		public void onSwipeRight() {
			if (viewFlipper.getDisplayedChild() > 0) {
				viewFlipper.showPrevious();
			}
		}

		public void onSwipeLeft() {
			if (viewFlipper.getDisplayedChild() < 3) {
				viewFlipper.showNext();
			}
		}

		public void onSwipeTop() {
		}

		public void onSwipeBottom() {
		}
	}
}