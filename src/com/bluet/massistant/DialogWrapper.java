package com.bluet.massistant;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

//--------------------------------------------------------------------------
	 public class DialogWrapper {
		    EditText titleField=null;
		    EditText valueField=null;
		    CheckBox visable = null;
		    
		    View base=null;
		    
		    DialogWrapper(View base) {
		      this.base=base;
		      valueField=(EditText)base.findViewById(R.id.setvalue);
		     
		    }
		    
		    String getTitle() {
		      return(getTitleField().getText().toString());
		    }
		    
		    float getValue() {
		      return(new Float(getValueField().getText().toString())
		                                                  .floatValue());
		    }
		    
		    private void setvtext(String str){
		    	TextView mtext=(TextView)base.findViewById(R.id.setvtext);
		    	mtext.setText(str);
		    }
		    
		    private void setttext(String str){
		    	TextView mtext=(TextView)base.findViewById(R.id.setttext);
		    	mtext.setText(str);
		    }
		    
		    void setnametext(String str){
		    	EditText mtext=(EditText)base.findViewById(R.id.settitle);
		    	//mtext.setText(str);
		    	mtext.setText(str);
		    }
		    
		    void setvaluetext(String str){
		    	EditText mtext=(EditText)base.findViewById(R.id.setvalue);
		    	//mtext.setText(str);
		    	mtext.setText(str);
		    }
		    
		    EditText getTitleField() {
		      if (titleField==null) {
		        titleField=(EditText)base.findViewById(R.id.settitle);
		      }
		      return(titleField);
		    }
		    
		    EditText getValueField() {
		      if (valueField==null) {
		        valueField=(EditText)base.findViewById(R.id.setvalue);
		      }
		      
		      return(valueField);
		    }
		    
		  }
