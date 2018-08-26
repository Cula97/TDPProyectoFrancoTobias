package francotobias.tdpproyecto;


import android.os.Handler;

public class BusUpdaterThread implements Runnable {
	private Handler handler;
	private boolean active;
	private Line line;

	public BusUpdaterThread(Line line) {
		handler = new Handler();
		active = false;
		this.line = line;
	}

	@Override
	public void run() {
		BusManager.updateBuses(line);

		if (isActive())
			handler.postDelayed(this, 10000);
	}

	public void start() {
		if (!isActive()) {
			active = true;
			handler.postDelayed(this, 10000);
		}
	}

	public void stop() {
		active = false;
	}

	public Line getLine() {
		return this.line;
	}

	public void setLine(Line line) {
		this.line = line;
	}

	public boolean isActive() {
		return active;
	}

}
