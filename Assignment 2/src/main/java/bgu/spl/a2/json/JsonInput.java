package bgu.spl.a2.json;
import java.util.Arrays;

import com.google.gson.annotations.SerializedName;

/**
 * A POJO that holds the data of the JSON input.
 *
 */
public class JsonInput {
	private Integer threads;
	@SerializedName("Computers")
	private JsonComputer[] computers;
	@SerializedName("Phase 1")
	private AbstractAction[] phase1;
	@SerializedName("Phase 2")
	private AbstractAction[] phase2;
	@SerializedName("Phase 3")
	private AbstractAction[] phase3;
	

	public Integer getThreads() {
		return threads;
	}

	public JsonComputer[] getComputers() {
		return computers;
	}

	public AbstractAction[] getPhase1() {
		return phase1;
	}

	public AbstractAction[] getPhase2() {
		return phase2;
	}

	public AbstractAction[] getPhase3() {
		return phase3;
	}

	@Override
	public String toString() {
		return "JsonInput \n"+ "\t[threads=" + threads + ", "+ "\tcomputers=" + Arrays.toString(computers) + "]";
	}

	public JsonInput(int threads, JsonComputer[] computers) {
		this.threads=new Integer(threads);
		this.computers=computers;
	}
	
	/**
	 * Nested JSON Computer POJO for Gson deserialization
	 */
	public class JsonComputer{
		@SerializedName("Type")
		private String type;
		@SerializedName("Sig Success")
		private long successSig;
		@SerializedName("Sig Fail")
		private long failSig;
		
		public JsonComputer(String type, long successSig, long failSig) {
			super();
			this.type = type;
			this.successSig = successSig;
			this.failSig = failSig;
		}

		public void setSuccessSig(long successSig) {
			this.successSig = successSig;
		}

		public void setFailSig(long failSig) {
			this.failSig = failSig;
		}

		public String getType() {
			return type;
		}

		public long getSuccessSig() {
			return successSig;
		}

		public long getFailSig() {
			return failSig;
		}
		@Override
		public String toString() {
			return "Computer [type=" + type + ", successSig=" + successSig + ", failSig=" + failSig + "]";
		}
	}
}
