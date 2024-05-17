package guess_num;


public class GuessNumManager {
	private static GuessNumManager instance = new GuessNumManager();

	private GuessNumManager() {
		/* singleton */}

	public static GuessNumManager getInstance() {
		return instance;
	}

//-------------------------------------------------------------------
	private int count = 5;
	private int targetNum = 0;
	public static final int RESULT_OK = 1;
	public static final int RESULT_BIG = 2;
	public static final int RESULT_SMALL = 3;

	public void init() {
		count = 5;
		targetNum = (int) (Math.random() * 100) + 1;
		System.out.println("targetNum : " + targetNum);
	}

	public int getcount() {
		return count;
	}

	public int judge(int userNum) {

			count--;
		if (userNum == targetNum) {
			return RESULT_OK;
		} else if (userNum < targetNum) {
			return RESULT_BIG;
		} else {
			return RESULT_SMALL;
		}
	}

}
