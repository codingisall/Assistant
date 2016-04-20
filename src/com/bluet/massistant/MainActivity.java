package com.bluet.massistant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

	
	private static final String TAG = "BT_Demo";
    private static final boolean D = true;
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	public static final byte LED_SERVO_COMMAND = 2;
	// Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "Device_name";
    public static final String TOAST = "Toast";
 // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    boolean Savebytes=false;
 // Array adapter for the conversation thread
    //private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
 // Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    public BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    public BluetoothChatService mChatService = null;
    public final Handler tHandler = new Handler(); 
     
    Timer tmrBlink;
	static Timer onpress;
	static Timer autosend;
    Timer RtmrBlink;
    Timer AutoSend,AutoSend2;  //自动发送定时器
    Timer AutoLink;
    static int RX_Count=0;
    static int TX_Count=0;
    // Layout Views
    private TextView mTitle;
    private TextView RX;
    private TextView TX,insendst;
    LinearLayout mLayout;
	TextView my_Text;
	CheckBox HEX_EN;
	EditText Edit1;
	String  last_ente_str;
	private ScrollView mScrollView;
	private Button Mconnectb,mSendButton,auto_sendbut;
	CircleButton butta,buttb;
	
	Timer systime,autosavet;private Time time;
	int year ,month,day,minute,hour,sec; 
	byte[] send1 = new byte[8];
	byte[] send2 = new byte[8];
	byte[] send3 = new byte[8];
	byte[] send4 = new byte[8];
	byte[] send5 = new byte[8];
	byte[] send6 = new byte[8];
	byte[] send7 = new byte[8];
	byte[] send8 = new byte[8];
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		my_Text=(TextView)findViewById(R.id.Text1);
        RX=(TextView)findViewById(R.id.RX_C);
        TX=(TextView)findViewById(R.id.TX_C);
        mScrollView=(ScrollView)findViewById(R.id.sv);
        mLayout = (LinearLayout) findViewById(R.id.layout);
        Edit1 = (EditText)findViewById(R.id.entry);
        mTitle=(TextView)findViewById(R.id.ST);
        HEX_EN=(CheckBox)findViewById(R.id.checkbox1);
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // 如果设备没有蓝牙模块。程序退出
            if (mBluetoothAdapter == null) {
               Toast.makeText(this, "Did not find the Bluetooth module, the program stops", Toast.LENGTH_LONG).show();
               finish();
               return;
           }
            
          //时间更新  500ms
            time = new Time();
            time.setToNow();
            systime = new Timer(500, new Runnable() {
                public void run() {
                	time.setToNow();
                	year = time.year;   
                    month = time.month +1; 
                    day = time.monthDay;   
                    minute = time.minute;   
                    hour = time.hour;   
                    sec = time.second;
                    //Log.i("Timer", "y:" + year+" m:"+ month+" d:"+day);
                    
                  }
              });
            systime.start();
            
            autosend = new Timer(20, new Runnable() {
                public void run() {
                	//sendMessage(data_buf);
                }});
            autosend.stop();
            
            autosavet = new Timer(5000, new Runnable() {
                public void run() {
                	Write_a_list();
                }});
            
            onpress = new Timer(2000, new Runnable() {
                public void run() {
                	onpress.stop();
                }});
            onpress.stop();
            
            tmrBlink = new Timer(100, new Runnable() {
                public void run() {
                 RX.setBackgroundColor(getResources().getColor(R.color.LED_G_OFF));
          		 RX.setTextColor(getResources().getColor(R.color.white_));
          		 
          		 tmrBlink.stop();
                }
              });  
            RtmrBlink = new Timer(100, new Runnable() {
                public void run() {
                TX.setBackgroundColor(getResources().getColor(R.color.LED_R_OFF));
          		 TX.setTextColor(getResources().getColor(R.color.white_));
          		 RtmrBlink.stop();
                }
              }); 
            
            Mconnectb = (Button)findViewById(R.id.nnectin);
            Mconnectb.setOnClickListener(new OnClickListener(){
            	public void onClick(View v){
            		if(mChatService.getState()!=3){
            		Intent serverIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            		}else{
            			mChatService.stop();
            		}
            		
            	}
            });
            
            mSendButton = (Button) findViewById(R.id.ok);
            mSendButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    // Send a message using content of the edit text widget
                    TextView view = (TextView) findViewById(R.id.entry);
                    String message = view.getText().toString();
                    if(message == last_ente_str);
                    else{
                    	last_ente_str = message;
                    	Save_config("last_str",last_ente_str);
                    }
                    sendMessage(message);
                }
            });  
          Resources res = getResources();
          int  c = res.getColor(R.color.keyn);
        send1[0]=(byte) 0x01;send1[1]=(byte) 0x05;send1[2]=(byte) 0x01;
      	send1[3]=(byte) 0x01;send1[4]=(byte) 0x08;send1[5]=(byte) 0x00;
      	send1[6]=(byte) 0xcc;send1[7]=(byte) 0x33;
         butta = (CircleButton) findViewById(R.id.cbutton1);
         butta.setColor(c); //设置按钮的着色
         butta.setText("进血");//按钮字符
         butta.set_datab(send1);
         butta.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                	sendHead();
                	sendMessage(send1);
                }} );
                
        send2[0]=(byte) 0x01;send2[1]=(byte) 0x09;send2[2]=(byte) 0x01;
     	send2[3]=(byte) 0x00;send2[4]=(byte) 0x0B;send2[5]=(byte) 0x00;
     	send2[6]=(byte) 0xCC;send2[7]=(byte) 0x33;
     	c = res.getColor(R.color.keyr);
         buttb = (CircleButton) findViewById(R.id.cbutton2);
         buttb.setColor(c); 
         buttb.setText("重启");
         buttb.set_datab(send2);
         buttb.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                	sendHead();
                	sendMessage(send2);		
                }} );
         
         
        send3[0]=(byte) 0x01;send3[1]=(byte) 0x07;send3[2]=(byte) 0x01;
     	send3[3]=(byte) 0x01;send3[4]=(byte) 0x09;send3[5]=(byte) 0x00;
     	send3[6]=(byte) 0xCC;send3[7]=(byte) 0x33;
     	c = res.getColor(R.color.keyn);
         buttb = (CircleButton) findViewById(R.id.cbutton21);
         buttb.setColor(c); 
         buttb.setText("清洗");
         buttb.set_datab(send3);
         buttb.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                	sendHead();
                	sendMessage(send3);		
                }} );
         
     	send4[0]=(byte) 0x01;send4[1]=(byte) 0x06;send4[2]=(byte) 0x01;
     	send4[3]=(byte) 0x02;send4[4]=(byte) 0x0A;send4[5]=(byte) 0x00;
     	send4[6]=(byte) 0xCC;send4[7]=(byte) 0x33;
         buttb = (CircleButton) findViewById(R.id.cbutton22);
         buttb.setColor(c); 
         buttb.setText("清空");
         buttb.set_datab(send4);
         buttb.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                	sendHead();
                	sendMessage(send4);		
                }} );
         
     	send5[0]=(byte) 0x01;send5[1]=(byte) 0x0A;send5[2]=(byte) 0x01;
     	send5[3]=(byte) 0x02;send5[4]=(byte) 0x0E;send5[5]=(byte) 0x00;
     	send5[6]=(byte) 0xCC;send5[7]=(byte) 0x33;
     	c = res.getColor(R.color.keyr);
         buttb = (CircleButton) findViewById(R.id.cbutton4);
         buttb.setColor(c); 
         buttb.setText("关闭");
         //buttb.set_datab(send5);
         buttb.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                	sendHead();
                	sendMessage(send5);		
                }} );
         
         //upload file
         butta = (CircleButton) findViewById(R.id.upload_file);
         butta.setColor(c); //设置按钮的着色
         butta.setText("上传文件");//按钮字符
         butta.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                File root = new File(Environment.getExternalStorageDirectory().toString().concat("/Assistant_Data/"));
                File[] files = root.listFiles();
                for (File file : files) {
                	file.getName();
                	Utils.UploadFile(file);
                }
                }} );
         // check update
         butta = (CircleButton) findViewById(R.id.check_update);
         butta.setColor(c); //设置按钮的着色
         butta.setText("检查更新");//按钮字符
         butta.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                	Intent intent = new Intent();
                    intent.setClass(MainActivity.this, UpdateActivity.class);
                	startActivity(intent);
                }} );
       // back
     	send6[0]=(byte) 0x01;send6[1]=(byte) 0x08;send6[2]=(byte) 0x01;
     	send6[3]=(byte) 0x01;send6[4]=(byte) 0x0B;send6[5]=(byte) 0x00;
     	send6[6]=(byte) 0xCC;send6[7]=(byte) 0x33;
     	c = res.getColor(R.color.keyn);
         buttb = (CircleButton) findViewById(R.id.cbutton3);
         buttb.setColor(c); 
         buttb.setText("后退");
         //buttb.set_datab(send6);
         buttb.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                	sendHead();
                	sendMessage(send6);		
                }} );
         
         AutoLink = new Timer(200, new Runnable() {
             public void run() {
            	 BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(lastdevice);
                 // Attempt to connect to the device
            	 if (mChatService != null)
                   mChatService.connect(device);
                 AutoLink.stop();
                 frist_conect = true;
             }});
         AutoLink.stop();
            
        last_ente_str = "";
        SharedPreferences config=this.getSharedPreferences("perference", MODE_PRIVATE);
		last_ente_str = config.getString("last_str", "");
		Edit1.setText(last_ente_str);
		
		lastdevice = config.getString("lastdevice_addr", "");
		
		if(lastdevice != ""){
			AutoLink.restart(); //200ms后尝试连接设备
			
		}else 
			is_conect();//询问连接设备
		String temp = Read_config("myadress");
		if((temp != "")&&(temp!=null)){
			if(isnumber(temp))
			my_add = (byte)(Integer.parseInt(temp)&0xff);
		}
		temp = Read_config("targetadress");
		if((temp != "")&&(temp!=null)){
			if(isnumber(temp))
			target_add = (byte)(Integer.parseInt(temp)&0xff);
		}
		
	}
    boolean frist_conect = false;
	void Save_config(String name,String Value){
		SharedPreferences  share = this.getSharedPreferences("perference", MODE_PRIVATE);  
        Editor editor = share.edit();//取得编辑器  
        editor.putString(name, Value);//存储配置 参数1 是key 参数2 是值   
        editor.commit();//提交刷新数据 
	}
	String Read_config(String name){
		String temp;
		SharedPreferences config=this.getSharedPreferences("perference", MODE_PRIVATE);
		temp = config.getString(name, "");
		return temp;
	}
	
	
	String lastdevice;
	void is_conect(){
		SharedPreferences config=this.getSharedPreferences("perference", MODE_PRIVATE);
		lastdevice = config.getString("lastdevice_addr", "");
		
		new AlertDialog.Builder(this).setTitle("连接请求").setIcon(android.R.drawable.ic_menu_info_details)
       	.setMessage("软件并没有连接蓝牙设备，请连接设备")
       	.setPositiveButton("连接上次的设备", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(lastdevice == ""){ //没有设备，那就连接新设备吧
						
						Intent serverIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
			            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			        }else{
			        	Log.d(TAG, "连接到最近的设备"+lastdevice);
			        	// Get the BLuetoothDevice object
		                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(lastdevice);
		                // Attempt to connect to the device
		                mChatService.connect(device);
			        }
				}
			})
			.setNegativeButton("连接新的设备", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// 
					Intent serverIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
		            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
				}
			}).show();
		
	}
	
	byte my_add=0x01,target_add=0x02;
	void sendHead(){
		byte[] buf = new byte[7];
		buf[0] = (byte)0xAA;
		buf[1] = (byte)0x55;
		buf[2] = (byte)0x01;
		buf[3] = (byte)my_add;
		buf[4] = (byte)target_add;
		buf[5] = (byte)0x02;
		buf[6] = (byte)0x04;
		sendMessage(buf);
	}
	
	private void AddSeting(String Title) {
	    
		   LayoutInflater inflater=LayoutInflater.from(this);
		    View addView=inflater.inflate(R.layout.set_address, null);
		    final DialogWrapper wrapper=new DialogWrapper(addView);
		    
		    wrapper.setnametext(""+my_add);
		    wrapper.setvaluetext(""+target_add);
		    
		    new AlertDialog.Builder(this)
		      .setTitle(Title)
		      .setView(addView)
		      .setPositiveButton("确定",
		                          new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog,
		                              int whichButton) {
		        	String Temp = wrapper.getTitleField().getText().toString();
		        	String tar = wrapper.getValueField().getText().toString();
		        	int value;
		        	//set_editor(wrapper.getTitleField().getText().toString(),wrapper.getValueField().getText().toString(),position,wrapper.read_vis());
		        	if(Temp.equals("")||tar.equals("")){
		        		Toast.makeText(getApplicationContext(), "输入的数据为空。放弃修改...", Toast.LENGTH_LONG).show();
		        		return;
		        	}
		        	
		        	if(isnumber(Temp)&&isnumber(tar)){
		        		value = Integer.valueOf(Temp);
		        		if(value >255){
		        			Toast.makeText(getApplicationContext(), "地址超过255。修改失败...", Toast.LENGTH_LONG).show();
			            	return;
		        		}
		        		value = Integer.valueOf(tar);
		        		if(value >255){
		        			Toast.makeText(getApplicationContext(), "地址超过255。修改失败...", Toast.LENGTH_LONG).show();
			            	return;
		        		}
		        		value = Integer.valueOf(Temp);
		        		my_add = (byte)(value&0xff);
		        		
		        		value = Integer.valueOf(tar);
		        		target_add = (byte)(value&0xff);
		        		
		            }else {
		            	Toast.makeText(getApplicationContext(), "输入中包含非整数。修改失败...", Toast.LENGTH_LONG).show();
		            	return;
		            }
		        	
		        	Save_config("targetadress",tar);
		        	Save_config("myadress",Temp);
		        }
		      })
		      .setNegativeButton("取消",
		                          new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog,
		                              int whichButton) {
		          // ignore, just dismiss
		        }
		      })
		      .show();
		  }
	
	boolean isnumber(String ST){
		char[] buf =ST.toCharArray();
		if(buf.length == 0)return false;
		for(int i=0;i<buf.length;i++){
			if(( buf[i]>'9')||( buf[i]<'0')){
				//if(( buf[i] != '-')&&( buf[i] != '.')&&( buf[i] != '+'))
					return false;
			}
		}
		return true;
	}
	
	static boolean press_on = false;
	static byte[] data_buf = new byte[8];
	public static void key_presssed(byte[] data){
		//data_buf = data;
		//press_on = true;
		//onpress.restart();
		//autosend.stop();
	}
	
	public static void key_off(){
		//press_on = false;
		//onpress.stop();
		//autosend.stop();
	}
	
	
	@Override
    public void onStart() {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");
        if (mBluetoothAdapter == null)return;
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
            if ((mChatService == null)) setupChat();
            if(D) Log.e(TAG, "++ setupChat ++");
        }
    }//----------onStart End-------------
    
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) {
        	mChatService.stop();
        }
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }//----------onDestroy End-------------
     
    @Override
    public synchronized void onResume() {
        super.onResume();
        
        if(D) Log.e(TAG, "+ ON RESUME +");
        
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
              // Start the Bluetooth chat services
              mChatService.start();
              if(D) Log.e(TAG, "mChatService.start");
            }
        }
      
        
   }//----------onResume End-------------
    @Override
    public void onPause(){
   	 super.onPause();
   	 if(D) Log.e(TAG, "- ON PAUSE -");
    }
    
    @Override
    public void onStop() {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }/**/
    
    private void setupChat() {
        Log.d(TAG, "setupConnect()");

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);
        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }
    
    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(byte[] send){
    	if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            //Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
    	Trig_TXLED(send.length);
        mChatService.write(send);
        // Reset out string buffer to zero and clear the edit text field
        mOutStringBuffer.setLength(0);
    }
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            //Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            //byte[] send = message.getBytes();
        	//String Encode = "gbk";
        	byte[] send = null;
        	if(HEX_EN.isChecked()!=true){
        	try{
        		send = message.getBytes("GBK");
        	}catch (UnsupportedEncodingException e) {
                // 
                e.printStackTrace();
        		}
        	}
        	else send=getStringhex(message);
        	Trig_TXLED(send.length);
            mChatService.write(send);
            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            //mOutEditText.setText(mOutStringBuffer);
        }
    }
   
 // The Handler that gets information back from the BluetoothChatService
    boolean firstconnect=false,trycon=false;
    
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothChatService.STATE_CONNECTED:
                    //mTitle.setText(R.string.title_connected_to);
                    //mTitle.append(mConnectedDeviceName);
                	Toast.makeText(getApplicationContext(), "已经连接到-"+mConnectedDeviceName, Toast.LENGTH_LONG).show();
                	mTitle.setText(R.string.title_connected_to);
                    mTitle.append(mConnectedDeviceName);
                    Mconnectb.setText("断开连接");
                    frist_conect = false;
                    findhead = 0;buf_index =0;frame_len = 0;
                   head_en = false;
                   if(Save_file_en == false)
                	   write_head();
                   autosavet.restart();
                	break;
                case BluetoothChatService.STATE_CONNECTING:
                    //mTitle.setText(R.string.title_connecting);
                	Toast.makeText(getApplicationContext(), "正在尝试连接远程设备...", Toast.LENGTH_LONG).show();
                	mTitle.setText(R.string.title_connecting);
                	break;
                case BluetoothChatService.STATE_LISTEN:
                case BluetoothChatService.STATE_NONE:
                    //mTitle.setText(R.string.title_not_connected);
                	mTitle.setText(R.string.title_not_connected);
                    Mconnectb.setText("连接设备");
                    if(frist_conect){
                    	frist_conect = false;
                    			is_conect(); 
                    }
                    autosavet.stop();
                    break;
                }
                break;
            case MESSAGE_WRITE:
                //byte[] writeBuf = (byte[]) msg.obj;
           	 // construct a string from the buffer
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                decode(readBuf,msg.arg1);
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                
                RX_Count=0;
                TX_Count=0;
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }

    };
    
    int findhead = 0,buf_index =0,frame_len = 0;
    boolean head_en = false;
    byte[] databuf = new byte[1024];
    void decode(byte[] buf,int len){
    	//my_add=0x01,target_add
    	for(int i=0;i<len;i++){
    		if(head_en==false){
    			switch (findhead){
    			case 0: if(buf[i] == (byte)0xAA)findhead++;else findhead = 0;
    						break;
    			case 1: if(buf[i] == (byte)0x55)findhead++;else findhead = 0;
    					break;
    			case 2: if(buf[i] == (byte)0x01)findhead++;else findhead = 0;
						break;
    			case 3: if(buf[i] == (byte)target_add)findhead++;else findhead = 0;
						break;
    			case 4: if(buf[i] == (byte)my_add)findhead++;else findhead = 0;
						break;
    			case 5: if(buf[i] == (byte)0x01){
    						head_en = true;
    						buf_index =0;
    						frame_len = 0;
    						Log.e("Decode", "find head!");
    						}
    					findhead = 0;
						break;
				default: findhead = 0;break;
    			}
    		}else{
    			
    			if(buf_index ==0){
    				frame_len = buf[i]&0xff;
    				Log.e("Decode", "frame_len "+frame_len);
    			}
    			databuf[buf_index++] = buf[i];
    			
    			if(buf_index == (frame_len+5)){ //收完一帧了。
    				get_data(databuf,frame_len+5);
    				findhead = 0;
    				head_en = false;
    				buf_index =0;
    				frame_len = 0;
    			}
    		}
    	}
    }
    
    
    void get_data(byte[] in, int len){
    	int i,isget = 0;
    	short  sum = 0,temp;
    	Log.e("Decode", "本帧需要校验字节" + ((in[0]&0xff)));
    	for(i=0;i<(in[0]&0xff);i++){
    		sum += (in[i+1]&0xff);
    	}
    	temp = (short) (((in[len-3]&0xff)<<8)|((in[len-4]&0xff)));
    	if(temp != sum){
    		Log.e("Decode", "check sum err!");
    		return;
    	}
    	Log.e("Decode", "check sum OK!");
    	int index = 2;
    	TextView view =null ;
    	isget = 0;
    	do{  //提取数据
    		Log.e("Decode", "当前   " +index);
    		if(in[index] == (byte)0x01){//速度
    			view = (TextView) findViewById(R.id.sp);
    			Log.e("Decode", "速度 ");
    		}
    		else if(in[index] == (byte)0x02){//加速度
    			view = (TextView) findViewById(R.id.acc);
    			Log.e("Decode", "加速度 ");
			    		}
    		else if(in[index] == (byte)0x03){//高度
    			view = (TextView) findViewById(R.id.high);
    			Log.e("Decode", "高度 ");
			}
    		else if(in[index] == (byte)0x04){//电量
    			view = (TextView) findViewById(R.id.pow);
    			Log.e("Decode", "电量 ");
			    		}
    		else if(in[index] == (byte)0x05){//左转向
    			view = (TextView) findViewById(R.id.left);
    			Log.e("Decode", "左转向 ");
			}
    		index++; //字节数量
			if(in[index] == (byte)0x01){
				temp = (short) (in[index+1]&0xff);
				Log.e("Decode", "1字节 ");
				index += 2;
				isget += 3;
			}else if(in[index] == (byte)0x02){
				Log.e("Decode", "2字节 ");
				temp = (short) (((in[index+2]&0xff)<<8)|((in[index+1]&0xff)));
				index += 3;
				isget += 4;
			}
    		
			if(view != null){
				view.setText(""+temp);
			}
    	}while(isget < len-6);
    	
    }
    
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.scan:
            // Launch the DeviceListActivity to see devices and do scan
        	Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        	
            return true;
        case R.id.sett:
        	AddSeting("地址设置");
        	break;
        case R.id.discoverable:
            // Ensure this device is discoverable by others
        	ensureDiscoverable();
            return true;
        case R.id.exit:
        	
        	break;
        }
        return true;
    }


    private void ensureDiscoverable() {
  	    if(D) Log.d(TAG, "ensure discoverable");
  	    if (mBluetoothAdapter.getScanMode() !=
  	        BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
  	        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
  	        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
  	        startActivity(discoverableIntent);
  	    }
  	}
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                // Get the device MAC address
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                Save_config("lastdevice_addr",address);
                // Get the BLuetoothDevice object
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                // Attempt to connect to the device
                mChatService.connect(device);
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
                setupChat();
                //("蓝牙启动成功...");
            } else {
                // User did not enable Bluetooth or an error occured
                Log.d(TAG, "Bluetooth not enabled");
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	 private Runnable mScrollToBottom = new Runnable() {     
	        @Override    
	        public void run() {     
	            //      
	            //Log.d("UI", "ScrollY: " + mScrollView.getScrollY());     
	            int off = mLayout.getMeasuredHeight() - mScrollView.getHeight();     
	            if (off > 0) {     
	                mScrollView.scrollTo(0, off);     
	            }                            
	        }     
	    };
	
	public void Trig_RXLED(int c){
   	 RX_Count+=c;
   	 RX.setText("RX "+RX_Count);
   	 
   	 RX.setBackgroundColor(getResources().getColor(R.color.LED_G_ON));
		 RX.setTextColor(getResources().getColor(R.color.Baclk));
		 
		 if(tmrBlink.getIsTicking())tmrBlink.restart();
		 else{
          tmrBlink.start();
          }
   }
	public void Trig_TXLED(int c){
	   	 TX_Count+=c;
	   	 TX.setText("TX "+TX_Count);
	   	 TX.setBackgroundColor(getResources().getColor(R.color.LED_R_ON));
		 TX.setTextColor(getResources().getColor(R.color.Baclk));
	    
		 if(RtmrBlink.getIsTicking())RtmrBlink.restart();
		 else{
	      RtmrBlink.start();
	      }
	   }
	static final char[] HEX_CHAR_TABLE = {
        '0', '1', '2', '3',
        '4', '5', '6', '7',
        '8', '9', 'A', 'B',
        'C', 'D', 'E', 'F'
      };    

      public static String getHexString(byte[] raw,int offset,int count) 
        throws UnsupportedEncodingException 
      {
    	 StringBuffer hex = new StringBuffer();
        for (int i=offset;i<offset+count;i++) {
          int v = raw[i]& 0xFF;
          hex.append(HEX_CHAR_TABLE[v >>> 4]);
          hex.append(HEX_CHAR_TABLE[v & 0xF]);
          hex.append(" ");
        }
        return hex.toString();
      }
      
      public  byte[] getStringhex(String ST)
      {
    	  ST=ST.replaceAll(" ", "");
    	  //Log.v("getStringhex",ST);
    	  char[] buffer =ST.toCharArray();
    	  byte[] Byte = new byte[buffer.length/2];
    	  int index=0;
    	  int bit_st=0;
    	  for(int i=0;i<buffer.length;i++)
    	  {
    		  int v=(int)(buffer[i]&0xFF);
    		  
    		  if(((v>47)&&(v<58)) || ((v>64)&&(v<71)) || ((v>96)&&(v<103))){
    			  if(bit_st==0){//高位
    				  Log.v("getStringhex","F True");
    				Byte[index]|= (getASCvalue(buffer[i])*16);
    				Log.v("getStringhex",String.valueOf(Byte[index]));
    				bit_st=1;
    			  }else {//低位
    				  Byte[index]|= (getASCvalue(buffer[i]));
    				  Log.v("getStringhex","F false");
    				  Log.v("getStringhex",String.valueOf(Byte[index]));
    				  bit_st=0;
      				index++;
    			  }
    		  }else if (v==32){ //空格
    			  Log.v("getStringhex","spance");
    			  if(bit_st==0);
    			  else{
    			  index++;
    			  bit_st=0;
    			  }
    		  }else continue;
    	  }
    	  bit_st=0;
    	  return Byte;
      }
      
      public static byte getASCvalue(char in)
      {
    	  byte out=0;
    	  switch(in){
    	  case '0':out=0;break;
    	  case '1':out=1;break;
    	  case '2':out=2;break;
    	  case '3':out=3;break;
    	  case '4':out=4;break;
    	  case '5':out=5;break;
    	  case '6':out=6;break;
    	  case '7':out=7;break;
    	  case '8':out=8;break;
    	  case '9':out=9;break;
    	  case 'a':out=10;break;
    	  case 'b':out=11;break;
    	  case 'c':out=12;break;
    	  case 'd':out=13;break;
    	  case 'e':out=14;break;
    	  case 'f':out=15;break;
    	  case 'A':out=10;break;
    	  case 'B':out=11;break;
    	  case 'C':out=12;break;
    	  case 'D':out=13;break;
    	  case 'E':out=14;break;
    	  case 'F':out=15;break;
    	  }
    	  return out;
      }
      
      boolean Save_file_en = false;
      String initial_name(){
    	    String temp;
	    	csv_fname = ""+year;
	    	if(month<10)
	    		csv_fname = csv_fname+"0";
	    	csv_fname = csv_fname + month;
	    	if(day<10)
	    		csv_fname = csv_fname+"0";
	    	csv_fname = csv_fname + day;
	    	if(hour<10)
	    		csv_fname = csv_fname+"0";
	    	csv_fname = csv_fname + hour;
	    	temp = csv_fname;
	    	if(minute<10)
	    		temp = temp+"0";
	    	temp = temp + minute;
	    	if(sec<10)
	    		temp = temp+"0";
	    	temp = temp + sec;/**/
	    	return temp;
	    }
      String get_time(){
  	    String temp;
  	  temp = ""+year+"-";
	    	if(month<10)
	    		temp = temp+"0";
	    	temp = temp + month+"-";
	    	if(day<10)
	    		temp = temp+"0";
	    	temp = temp + day+" ";
	    	if(hour<10)
	    		temp = temp+"0";
	    	temp = temp + hour+":";
	    	if(minute<10)
	    		temp = temp+"0";
	    	temp = temp + minute+":";
	    	if(sec<10)
	    		temp = temp+"0";
	    	temp = temp + sec;/**/
	    	return temp;
	    }
    //------------------------------------------------------------------------    
	    public void write_head(){
	    	String head;
	    	initial_name();
	    	head = "目标地址,速度,加速度,电量,左转向,高度,时间\r\n";
	    	mual_file = "Rec-"+csv_fname;
	    	Write_File(head,mual_file);
	    	Save_file_en = true;
	    	autosavet.restart();
	    	Log.v("file",mual_file + "    "+auto_file);
	    }
	    
	    //------------------------------------------------------------------------
	    void Write_a_list(){
			String  data=target_add+",";
			TextView view;
			//data = "测试版本的软件，没有记录功能"+Latitude+","+Longitude;
			view = (TextView) findViewById(R.id.sp);
			data += view.getText()+",";
			
			view = (TextView) findViewById(R.id.acc);
			data += view.getText()+",";
			
			view = (TextView) findViewById(R.id.pow);
			data += view.getText()+",";
			
			view = (TextView) findViewById(R.id.left);
			data += view.getText()+",";
			
			view = (TextView) findViewById(R.id.high);
			data += view.getText()+","+get_time()+"\r\n";
			
			Write_File(data,mual_file);
			Log.v("file","Write_a_list");
		}
	    //---------------------------------------------------------------
	         //numb_tt,filent;
	         String mual_file,auto_file="",csv_fname;
	         public void Write_File(String str,String file_name)
	         {
	         	//Calendar   c   =   Calendar.getInstance();  
	            // Date   date   =   c.getTime();  
	            // File destDir = new File("/data/data/lisn3188.chip7/files/"); 
	             File destDir = new File("/sdcard/Assistant_Data/"); 
	        	 if (!destDir.exists()) {
	                 destDir.mkdirs();//创建文件夹
	                }
	          try 
	          {
		           FileOutputStream outStream = new FileOutputStream("/sdcard/Assistant_Data/"+file_name+".txt",true);
		           OutputStreamWriter writer = new OutputStreamWriter(outStream,"gb2312");
		           writer.write(str);
		           writer.flush();
		           writer.close();//记得关闭
		           outStream.close();
	           }
	          catch (Exception e)
	          {
	           Toast.makeText(getApplicationContext(), "/sdcard/Assistant_Data/"+".txt 错误", Toast.LENGTH_SHORT).show();
	        	  //show_msg("/sdcard/GPS Save/"+file_name+".txt 错误");
	        	  Log.e("m", "file write error");
	          }
	         }
	
}
