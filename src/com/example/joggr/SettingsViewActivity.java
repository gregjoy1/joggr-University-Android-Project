package com.example.joggr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsViewActivity extends Activity {

	private Settings _settingsModel;
	
	private TextView _usernameTextBox;

	private RadioButton _radioButtonInterval5;
	private RadioButton _radioButtonInterval10;
	private RadioButton _radioButtonInterval30;
	private RadioButton _radioButtonInterval60;
	
	private CheckBox _autoUploadCheckBox;
	
	private Button _saveButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings_view);

		this._settingsModel = new Settings(this.getApplicationContext());
		
		this._initUsernameTextView();
		this._initRadioButtons();
		this._initAutoUploadCheckBox();
		this._initSaveButton();
				
		Toast.makeText(this.getApplicationContext(), "Settings Loaded...", Toast.LENGTH_SHORT).show();
	}
	
	private void _initUsernameTextView() {
		this._usernameTextBox = (TextView) this.findViewById(R.id.usernameText);
		this._usernameTextBox.setText(this._settingsModel.getUsername());
	}
	
	private void _initRadioButtons() {
		this._radioButtonInterval5 = (RadioButton) this.findViewById(R.id.radioInterval5);
		this._radioButtonInterval10 = (RadioButton) this.findViewById(R.id.radioInterval10);
		this._radioButtonInterval30 = (RadioButton) this.findViewById(R.id.radioInterval30);
		this._radioButtonInterval60 = (RadioButton) this.findViewById(R.id.radioInterval60);
		this._setLoggingIntervalToRadioButtons(this._settingsModel.getLoggingIncrements());
	}
	
	private void _initAutoUploadCheckBox() {
		this._autoUploadCheckBox = (CheckBox) this.findViewById(R.id.uploadRunChk);
		this._autoUploadCheckBox.setChecked(this._settingsModel.doesAppAutoUpload());
	}
	
	private void _initSaveButton() {
		this._saveButton = (Button) this.findViewById(R.id.saveBtn);
		this._saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				_settingsModel.setUsername(_getUsernameFromTextField());
				_settingsModel.setLoggingIncrements(_getLoggingIntervalFromRadioButtons());
				_settingsModel.setIfAppAutoUploads(_getAutoUploadFromCheckBox());
				_settingsModel.saveSettings();
				
				Toast.makeText(getApplicationContext(), "Settings Saved...", Toast.LENGTH_SHORT).show();
				
				_returnToMainMenu();
				
			}
			
		});
	}
	
	private int _getLoggingIntervalFromRadioButtons() {
		if(this._radioButtonInterval5.isChecked()) {
			return 5;
		} else if(this._radioButtonInterval10.isChecked()) {
			return 10;
		} else if(this._radioButtonInterval30.isChecked()) {
			return 30;
		} else if(this._radioButtonInterval60.isChecked()) {
			return 60;
		} else {
			return 5;
		}
	}
	
	private void _setLoggingIntervalToRadioButtons(int loggingInterval) {
		switch(loggingInterval) {
			case 5:
				this._radioButtonInterval5.setChecked(true);
				break;
			case 10:
				this._radioButtonInterval10.setChecked(true);
				break;
			case 30:
				this._radioButtonInterval30.setChecked(true);
				break;
			case 60:
				this._radioButtonInterval60.setChecked(true);
				break;
			default:
				this._radioButtonInterval5.setChecked(true);
				break;
		}
	}
	
	private String _getUsernameFromTextField() {
		return this._usernameTextBox.getText().toString();
	}
	
	private Boolean _getAutoUploadFromCheckBox() {
		return this._autoUploadCheckBox.isChecked();
	}
	
	private void _returnToMainMenu() {
		this.startActivity(new Intent(this, MainActivity.class));
	}
}
