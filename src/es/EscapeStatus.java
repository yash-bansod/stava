package es;

import soot.SootField;

import java.util.HashSet;
import java.util.Iterator;

public class EscapeStatus {

	public HashSet<EscapeState> status;
	public boolean recaptureFlag;

	public EscapeStatus() {
		status = new HashSet<>();
		status.add(NoEscape.getInstance());
		recaptureFlag = false;
	}

	public EscapeStatus(EscapeState es) {
		status = new HashSet<>();
		status.add(es);
		recaptureFlag = false;
	}

	public EscapeStatus(EscapeState es, boolean r) {
		status = new HashSet<>();
		status.add(es);
		recaptureFlag = r;
	}

	public EscapeStatus(EscapeStatus es) {
		status = new HashSet<>();
		status.addAll(es.status);
		recaptureFlag = es.recaptureFlag;
	}

	@Override
	public String toString() {
		return status.toString() + " [Recapture]: " + String.valueOf(recaptureFlag);
	}

	public void purge() {
		if (containsCV()) {
			if (doesEscape()) purgeAndEscape();
			else if (containsNoEscape()) {
				status.remove(NoEscape.getInstance());
			}
		} else if (doesEscape() && containsNoEscape()) purgeAndEscape();
	}

	public void purgeAndEscape() {
		HashSet<EscapeState> es = new HashSet<EscapeState>();
		es.add(Escape.getInstance());
		status = es;
	}

	public void setEscape() {
		purgeAndEscape();
	}

	public void setNoEscape() {
		if (!containsCV() && !doesEscape()) {
			// the set may actually be empty
			status.add(NoEscape.getInstance());
		}
	}

	@Override
	public int hashCode() {
		return status.hashCode();
	}

	public void addEscapeState(EscapeState es) {
		if (es != null) status.add(es);
		purge();
	}

	public void addEscapeState(EscapeState es, boolean r) {
		if (es != null){
			status.add(es);
			if(!recaptureFlag)
				recaptureFlag = r;
		} 
		purge();
	}

	public void addEscapeStatus(EscapeStatus es) {
		if (es != null) {
			this.status.addAll(es.getStatus());
			if(!this.recaptureFlag)
				this.recaptureFlag = es.getRecapture();
		}
		purge();
	}

	public boolean doesEscape() {
		return status.contains(Escape.getInstance());
	}

	public boolean containsNoEscape() {
		return status.contains(NoEscape.getInstance());
	}

	public boolean containsCV() {
		boolean _ret = false;
		Iterator<EscapeState> it = this.status.iterator();
		while (it.hasNext()) {
			EscapeState es = it.next();
			if (es instanceof ConditionalValue) {
				return true;
			}
		}
		return _ret;
	}

	public HashSet<EscapeState> getStatus() {
		return status;
	}

	public void setStatus(HashSet<EscapeState> status) {
		this.status = status;
	}

	public boolean isCallerOnly() {
		boolean _ret = true;
		Iterator<EscapeState> it = status.iterator();
		while (it.hasNext()) {
			EscapeState e = it.next();
			if (e instanceof Escape || e instanceof NoEscape || (e instanceof ConditionalValue && (((ConditionalValue) e).getMethod() != null))) {
				_ret = false;
				break;
			}
		}
		return _ret;
	}

	public EscapeStatus makeField(SootField f) {
		EscapeStatus _ret = new EscapeStatus();
		Iterator<EscapeState> it = status.iterator();
		while (it.hasNext()) {
			EscapeState e = it.next();
			if (!(e instanceof ConditionalValue)) {
				_ret.addEscapeState(e);
			} else {
				_ret.addEscapeState(((ConditionalValue) e).addField(f));
			}
		}
		return _ret;
	}

	public EscapeStatus makeFalseClone() {
		if (this.doesEscape() || this.containsNoEscape()) return this;
		EscapeStatus _ret = new EscapeStatus();
		this.status.forEach(cv -> {
			_ret.addEscapeState((((ConditionalValue) cv).makeFalseClone()));
		});
		return _ret;
	}

	public boolean getRecapture(){
		return this.recaptureFlag;
	}

	public void setRecapture(boolean r){
		this.recaptureFlag = r;
	}

}
