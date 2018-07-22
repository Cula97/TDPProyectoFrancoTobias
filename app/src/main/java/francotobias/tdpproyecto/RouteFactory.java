package francotobias.tdpproyecto;
/*
import android.location.Location;

import java.util.List;

public class RouteFactory {
	protected static Route l319, l500, l502, l503, l504, l504EX, l505, l506, l507, l509, l512, l513, l513EX, l514, l516, l517, l518, l519, l519A, l520;

	public static Route getL319() {
		if (l319 == null) {
			List<Location> routeGo, routeReturn;
			//routeGo = {new Location(), new Location(), };
			//routeReturn = {new Location(), new Location(), };
			l319 = new Route(routeGo, routeReturn);
		}

		return l319;
	}

	public static Route getL500() {
		if (l500 == null) {
			List<Location> routeGo, routeReturn;
			//routeGo = {new Location(), new Location(), };
			//routeReturn = {new Location(), new Location(), };
			l500 = new Route(routeGo, routeReturn);
		}

		return l500;
	}

	public static Route getL502() {
		if (l502 == null) {
			List<Location> routeGo, routeReturn;
			//routeGo = {new Location(), new Location(), };
			//routeReturn = {new Location(), new Location(), };
			l502 = new Route(routeGo, routeReturn);
		}

		return l502;
	}

	public static Route getL503() {
		if (l503 == null) {
			List<Location> routeGo, routeReturn;
			//routeGo = {new Location(), new Location(), };
			//routeReturn = {new Location(), new Location(), };
			l503 = new Route(routeGo, routeReturn);
		}

		return l503;
	}

	public static Route getL504() {
		if (l504 == null) {
			List<Location> routeGo, routeReturn;
			//routeGo = {new Location(), new Location(), };
			//routeReturn = {new Location(), new Location(), };
			l504 = new Route(routeGo, routeReturn);
		}

		return l504;
	}

	public static Route getL504EX() {
		if (l504EX == null) {
			List<Location> routeGo, routeReturn;
			//routeGo = {new Location(), new Location(), };
			//routeReturn = {new Location(), new Location(), };
			l504EX = new Route(routeGo, routeReturn);
		}

		return l504EX;
	}

	public static Route getL505() {
		if (l505 == null) {
			List<Location> routeGo, routeReturn;
			//routeGo = {new Location(), new Location(), };
			//routeReturn = {new Location(), new Location(), };
			l505 = new Route(routeGo, routeReturn);
		}

		return l505;
	}

	public static Route getL506() {
		if (l506 == null) {
			List<Location> routeGo, routeReturn;
			//routeGo = {new Location(), new Location(), };
			//routeReturn = {new Location(), new Location(), };
			l506 = new Route(routeGo, routeReturn);
		}

		return l506;
	}

	public static Route getL507() {
		if (l507 == null) {
			List<Location> routeGo, routeReturn;
			//routeGo = {new Location(), new Location(), };
			//routeReturn = {new Location(), new Location(), };
			l507 = new Route(routeGo, routeReturn);
		}

		return l507;
	}

	public static Route getL509() {
		if (l509 == null) {
			List<Location> routeGo, routeReturn;
			//routeGo = {new Location(), new Location(), };
			//routeReturn = {new Location(), new Location(), };
			l509 = new Route(routeGo, routeReturn);
		}

		return l509;
	}

	public static Route getL512() {
		if (l512 == null) {
			List<Location> routeGo, routeReturn;
			//routeGo = {new Location(), new Location(), };
			//routeReturn = {new Location(), new Location(), };
			l512 = new Route(routeGo, routeReturn);
		}

		return l512;
	}

	public static Route getL513() {
		if (l513 == null) {
			List<Location> routeGo, routeReturn;
			//routeGo = {new Location(), new Location(), };
			//routeReturn = {new Location(), new Location(), };
			l513 = new Route(routeGo, routeReturn);
		}

		return l513;
	}

	public static Route getL513EX() {
		if (l513EX == null) {
			List<Location> routeGo, routeReturn;
			//routeGo = {new Location(), new Location(), };
			//routeReturn = {new Location(), new Location(), };
			l513EX = new Route(routeGo, routeReturn);
		}

		return l513EX;
	}

	public static Route getL514() {
		if (l514 == null) {
			List<Location> routeGo, routeReturn;
			//routeGo = {new Location(), new Location(), };
			//routeReturn = {new Location(), new Location(), };
			l514 = new Route(routeGo, routeReturn);
		}

		return l514;
	}

	public static Route getL516() {
		if (l516 == null) {
			List<Location> routeGo, routeReturn;
			//routeGo = {new Location(), new Location(), };
			//routeReturn = {new Location(), new Location(), };
			l516 = new Route(routeGo, routeReturn);
		}

		return l516;
	}

	public static Route getL517() {
		if (l517 == null) {
			List<Location> routeGo, routeReturn;
			//routeGo = {new Location(), new Location(), };
			//routeReturn = {new Location(), new Location(), };
			l517 = new Route(routeGo, routeReturn);
		}

		return l517;
	}

	public static Route getL518() {
		if (l518 == null) {
			List<Location> routeGo, routeReturn;
			//routeGo = {new Location(), new Location(), };
			//routeReturn = {new Location(), new Location(), };
			l518 = new Route(routeGo, routeReturn);
		}

		return l518;
	}

	public static Route getL519() {
		if (l519 == null) {
			List<Location> routeGo, routeReturn;
			//routeGo = {new Location(), new Location(), };
			//routeReturn = {new Location(), new Location(), };
			l519 = new Route(routeGo, routeReturn);
		}

		return l519;
	}

	public static Route getL519A() {
		if (l519A == null) {
			List<Location> routeGo, routeReturn;
			//routeGo = {new Location(), new Location(), };
			//routeReturn = {new Location(), new Location(), };
			l519A = new Route(routeGo, routeReturn);
		}

		return l519A;
	}

	public static Route getL520() {
		if (l520 == null) {
			List<Location> routeGo, routeReturn;
			//routeGo = {new Location(), new Location(), };
			//routeReturn = {new Location(), new Location(), };
			l520 = new Route(routeGo, routeReturn);
		}

		return l520;
	}
}
*/