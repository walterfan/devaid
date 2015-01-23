var linkModel = Backbone.Model.extend({
    defaults: {
    	id: 0,
        title: '',
        url:'',
        category:'',
        tag:''
    },

    validation: function(attrs) {
    	title: {
    		required: true
    	}
    }

});


var linkList = Backbone.Collection.extend({ //World 对象的集合
	initialize: function (models, options) {
		this.bind("add", options.view.showmodel); 
	}
});


var linkView = Backbone.View.extend({
	el: $("#container"),
	initialize: function() {
		this.links = new linkList(null, {view : this})
	},
	
	events: {
		"click #btnSubmit": "addmodel"
	},
	
	addmodel: function() {
		var link = new linkModel({
			title: $("#title").val(),
			url: $("#url").val(),
			tag: $("#tag").val(),
		});
		this.links.add(link);
		console.log("add model");
	},
	
	showmodel: function(model) {
		this.template = _.template("#linktpl").html();
		$('#linkTab tr').last().after(this.template(model.toJSON()));
		console.log("show model");
	},
	
	render: function() {
		console.log("render...");
	}
	
});

var appView = new linkView();
