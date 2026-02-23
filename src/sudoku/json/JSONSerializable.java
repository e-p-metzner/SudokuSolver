package sudoku.json;

import java.io.Serializable;

import processing.data.JSONObject;

public interface JSONSerializable extends Serializable {

	public JSONObject serialize2json();

	public void deserializeFromJson(JSONObject json);

}
