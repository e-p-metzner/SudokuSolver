package sudoku.gameplay;

import java.util.ArrayList;
import java.util.List;

import sudoku.rules.AllDigitsDistinct;

public class Cell {

	private boolean given;
	private boolean active;
	private char content;
	private String innerMarks;
	private String outerMarks;
	private List<Character> candidates;
	private char[] available;
	private List<AllDigitsDistinct> visible_sets;

	public Cell() {
		given = false;
		active = true;
		content = ' ';
		innerMarks = "";
		outerMarks = "";
		available = new char[0];
		candidates = new ArrayList<Character>();
		visible_sets = new ArrayList<AllDigitsDistinct>();
	}

	public boolean isSolved() {
		return content != ' ';
	}

	public char getContent() {
		return content;
	}

	public void setContent(char content) {
		this.content = content;
		this.candidates.clear();
		this.candidates.add(content);
		this.available = new char[] { content };
	}

	public boolean isGiven() {
		return given;
	}

	public void setGiven(char content) {
		this.content = content;
		this.given = true;
	}

	public boolean isActive() {
		return active;
	}

	public void setActivation(boolean active) {
		this.active = active;
	}

	public String getInnerMarks() {
		return innerMarks;
	}

	public void setInnerMarks(String inner) {
		innerMarks = inner;
	}

	public String getOuterMarks() {
		return outerMarks;
	}

	public void setOuterMarks(String outer) {
		outerMarks = outer;
	}

	public char[] getAvailables() {
		return available.clone();
	}

	public void setAvailables(char[] availables) {
		available = availables.clone();
	}

	public void addAvailable(char candidate) {
		for (char a : available) {
			if (a == candidate) {
				return;
			}
		}
		char[] a2 = new char[available.length + 1];
		a2[available.length] = candidate;
		for (int i = 0; i < available.length; i++) {
			a2[i] = available[i];
		}
		available = a2;
	}

	public boolean removeAvailable(char candidate) {
		boolean isInAvailables = false;
		for (char a : available) {
			if (a == candidate) {
				isInAvailables = true;
				break;
			}
		}
		if (!isInAvailables) {
			return false;
		}
		char[] a2 = new char[available.length - 1];
		int j = 0;
		for (int i = 0; i < available.length; i++) {
			if (available[i] == candidate) {
				continue;
			}
			a2[j++] = available[i];
		}
		available = a2.clone();
		return true;
	}

	public List<Character> getCandidates() {
		return candidates;
	}

	public char[] getCandidatesArray() {
		char[] cc = new char[candidates.size()];
		for (int i = 0; i < cc.length; i++) {
			cc[i] = candidates.get(i);
		}
		return cc;
	}

	public void addCandidate(char c) {
		boolean isAvailable = false;
		for (char a : available) {
			if (a == c) {
				isAvailable = true;
				break;
			}
		}
		if (!isAvailable) {
			return;
		}
		Character candidate = c;
		if (!candidates.contains(candidate)) {
			candidates.add(candidate);
		}
	}

	public void addAllCandidates(Character[] c_arr) {
		for (Character c : c_arr) {
			addCandidate(c);
		}
	}

	public void removeCandidate(char c) {
		Character candidate = c;
		if (candidates.contains(candidate)) {
			candidates.remove(candidate);
		}
	}

	public void clearCandidates() {
		candidates.clear();
	}

	public void candidates2innermarks() {
		innerMarks = "";
		candidates.sort(Character::compare);
		for (Character c : candidates) {
			innerMarks += c;
		}
	}

	public List<AllDigitsDistinct> getVisibleSets() {
		return visible_sets;
	}

	public boolean sees_cell(int col, int row) {
		for (AllDigitsDistinct set : visible_sets) {
			int[] cc = set.getCells();
			for (int u = 0; u < cc.length; u += 2) {
				if (cc[u] == col && cc[u + 1] == row) {
					return true;
				}
			}
		}
		return false;
	}
}
