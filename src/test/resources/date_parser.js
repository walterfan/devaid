//--------------------------mock script-------------------------------------//
var cc = {};

cc.Variable = function(name) {
    this.name= name;
    this.value="";
    this.setValue=function(val) {
	    this.value = val;
	}
	
	this.getValue=function(val) {
	     return this.value;
	}
	
	this.toString = function() {
		return "" + this.value;
	}
};

cc.Context = function() {
    this.G_BinaryName = new cc.Variable("G_BinaryName");
    this.logfileName = new cc.Variable("logfileName");
};


var context = new cc.Context();
//--------------------------test script-------------------------------------//
context.G_BinaryName.setValue("waltertest");
var date, s;
var now = new  Date();                    
s = now.getMonth() + 1;
if (s < 10)  s = '0' + s;
date = s+'';
s = now.getDate();
if (s < 10)  s = '0' + s;
date += s;
date += now.getFullYear();
var logfile;
logfile=context.G_BinaryName.toString()+'_hacker_'+date+'.log';
context.logfileName.setValue(logfile);

print("result: " + context.logfileName.toString());