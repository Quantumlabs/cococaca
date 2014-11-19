_DEBUG = false;
_LOG_ON = false;
function card_content_on_click(e) {
	var eve = e || window.event;
	var x = eve.clientX, // 相对于客户端的X坐标
	y = eve.clientY, // 相对于客户端的Y坐标
	x1 = eve.screenX, // 相对于计算机屏幕的X坐标
	y1 = eve.screenY; // 相对于计算机屏幕的Y坐标

	alert("相对客户端的坐标：\n" + "x = " + x + "\n" + "y = " + y + "\n\n"
			+ "相对屏幕的坐标：\n" + "x = " + x1 + "\n" + "y = " + y1);
}

log_debug = function() {
	if (!_LOG_ON) {
		return;
	}
	message = arguments[0];
	alert(sformat("[DEBUG]{0}", message));
}

sformat = function() {
	// The string containing the format items (e.g. "{0}")
	// will and always has to be the first argument.
	var theString = arguments[0];

	// start with the second argument (i = 1)
	for (var i = 1; i < arguments.length; i++) {
		// "gm" = RegEx options for Global search (more than one instance)
		// and for Multiline search
		var regEx = new RegExp("\\{" + (i - 1) + "\\}", "gm");
		theString = theString.replace(regEx, arguments[i]);
	}

	return theString;
}

/**
 * 500range primary color from Material_Design#UI-palette
 * 
 * @see http://www.google.com/design/spec/style/color.html#color-ui-color-palette
 */
/**
 * ****************************@ Globe variables definition
 * start.*****************************
 */
var primary_color_map = {};
primary_color_map[0] = "#607d8b";
primary_color_map[1] = "#e51c23";
primary_color_map[2] = "#e91e63";
primary_color_map[3] = "#9c27b0";
primary_color_map[4] = "#673ab7";
primary_color_map[5] = "#3f51b5";
primary_color_map[6] = "#5677fc";
primary_color_map[7] = "#03a9f4";
primary_color_map[8] = "#00bcd4";
primary_color_map[9] = "#009688";
primary_color_map[10] = "#259b24";
primary_color_map[11] = "#8bc34a";
primary_color_map[12] = "#cddc39";
primary_color_map[13] = "#ffeb3b";
primary_color_map[14] = "#ffc107";
primary_color_map[15] = "#ff9800";
primary_color_map[16] = "#ff5722";
primary_color_map[17] = "#795548";
primary_color_map[18] = "#9e9e9e";

var primary_color_state = -1;

_URL_COMMENTED_USER_FORMAT = "post/commented?ID={0}";
_URL_CDN_AVATAR_FORMAT = "../Img/{0}";
_URL_POSTS_BATCH_RETRIEVE_STATIC = "../Post/";
_URL_CDN_CONTENT_IMG_FORMAT = "../Img/{0}"

/**
 * ****************************@ Globe variables definition done.
 * *****************************
 */

function do_stateful_pseudo_random_pick() {
	idx = primary_color_state % 18;
	primary_color_state += 1;
	return idx;
}

function fill_backgroud_color_forall_card_content() {
	var card_containers = $("div[name=card-container]");
	var length = card_containers.length;
	for (var i = 0; i < length; i++) {
		var card_container = card_containers[i];
		card_container.style.background = primary_color_map[do_stateful_pseudo_random_pick()];
	}
}

/**
 * Create commented users inside #show-viewer for specified post.
 */
function create_viewers_in_post(post_ID, user_IDs) {
	var post_div = $(sformat("div[id={0}]", post_ID));
	var viewer_div = post_div.find("div[name=show-viewer]");
	for ( var user_ID in user_IDs) {
		var div = $("<div></div>");
		div.class = "col-xs-2";
		var avatar_img = $(sformat(
				"<img src=\"{0}\" class=\"img-circle center-block img-responsive\"/>",
				sformat(_URL_CDN_AVATAR_FORMAT, user_ID)));
		avatar_img.appendTo(div);
		div.appendTo(viewer_div);
	}
}

function load_commented_users(post_ID) {
	$.ajax({
		url : sformat(_URL_COMMENTED_USER_FORMAT, post_ID),
		success : function(data, status, xhr) {
			alert(data + status);
			var user_IDs_json = eval(data);
			create_viewers_in_post(post_ID, user_IDs_json);
		},
		error : function(xhr, status, err) {
			if (_DEBUG) {
				log_debug(status);
			}
		}
	});
}

function build_card_header(post_info) {
	var card_head = $("<div name=\"card-header\" class=\"container\"></div>");
	var post_header_row = $("<div class=\"row\"></div>");
	var post_author_avatar_container = $("<div class=\"col-md-2\"  name=\"card-header-author-avatar\"></div>");
	var post_author_avatar = $(sformat(
			"<a><img class=\"img-circle img-responsive\" src=\"{0}\" /></a>",
			_DEBUG ? "http://placehold.it/65x65&text=avatar" : sformat(
					_URL_CDN_AVATAR_FORMAT, post_info.author)));
	post_author_avatar.appendTo(post_author_avatar_container);
	post_author_avatar_container.appendTo(post_header_row);
	post_header_row.appendTo(card_head)
	return card_head;
}

function build_card_content_container(post_info) {
	var card_content_container = $("<div name=\"card-content-container\"></div>");
	var inner_card_content_container = $("<div name=\"inner-card-content-container\"onclick=\"javascript:card_content_on_click()\"></div>");
	var img = $(sformat(
			"<img class=\"img-responsive center-block\"  src=\"{0}\">",
			_DEBUG ? "http://placehold.it/600x600&text=avatar" : sformat(
					_URL_CDN_CONTENT_IMG_FORMAT, post_info.content)));
	img.appendTo(inner_card_content_container);
	inner_card_content_container.appendTo(card_content_container);
	build_danmuku(post_info).appendTo(inner_card_content_container);
	return card_content_container;
}

function build_danmuku(post_info) {
	var danmuku_content_container = $("<div name=\"card_danmuku_content_container\"></div>");
	// var card_danmuku_content_container =
	// $(parent_card_content_container).find("div[name=card_danmuku_content_container]");
	for (var idx = 0; idx < post_info.danmuku.length; idx++) {
		var danmuku = post_info.danmuku[idx];
		// danmuku = {author:<author>, content:<content>, timestamp:<timestamp>}
		var danmuku_div = $(sformat(
				"<div class=\"item\"><div name=\"danmuku-author\" class=\"danmuku-init img-circle\"></div>{0}</div>",
				danmuku.content));
		danmuku_div.appendTo(danmuku_content_container);
	}
	return danmuku_content_container;
}

/**
 * @TODO Actions are not added, e.g. liked, repost, share etc.
 */
function build_card_foot(post_info) {
	var card_foot = $("<div name=\"card-foot\" class=\"container-fluid\"></div>");
	var card_foot_row_content_description = $(sformat(
			"<div class=\"h3\" name=\"content-description\">{0}</div>",
			post_info.description));
	card_foot_row_content_description.appendTo(card_foot);

	var card_foot_row_show_viewer = $("<div name=\"show-viewer\" class=\"row\"></div>");
	var script = $("<script> load_commented_users(\"{0}\")</script>",
			post_info.postID)
	script.appendTo(card_foot_row_show_viewer);
	card_foot_row_show_viewer.appendTo(card_foot);

	var card_foot_row_comment_text_input = $("<div class=\"form-group\" name=\"comment-text-input\"></div>");
	var comment_input = $("<input class=\"form-control input-sm\" type=\"text\" placeholder=\":)\">");
	var comment_submit_button = $("<button class=\"input-sm btn btn-primary pull-right\"></button>");
	var comment_submit_button_icon = $("<span name=\"comment-text-input-btn\" class=\"glyphicon glyphicon-comment\"></span>");
	comment_submit_button_icon.appendTo(comment_submit_button);
	comment_input.appendTo(card_foot_row_comment_text_input);
	comment_submit_button.appendTo(card_foot_row_comment_text_input);

	card_foot_row_comment_text_input.appendTo(card_foot);

	return card_foot;
}

function build_post_item(post_info) {
	var row = $("<div name=\"post-item\"class=\"row\" id=\"" + post_info.postID
			+ "\"></div>");

	/* This is for place holding, in order to centralize card_container */
	$("<div class=\"col-md-2\"></div>").appendTo(row);

	var card_container = $(sformat("<div class=\"col-md-8\" name=\"card-container\"></div>"));
	card_container.appendTo(row);
	build_card_header(post_info).appendTo(card_container);
	build_card_content_container(post_info).appendTo(card_container);
	build_card_foot(post_info).appendTo(card_container);
	$("<div class=\"col-md-2\"></div>").appendTo(row);

	return row
}
function create_posts() {
	$.ajax({
		url : _URL_POSTS_BATCH_RETRIEVE_STATIC,
		success : function(data, status, xhr) {
			var main_container = $("div[name=main-container]");
			var posts = eval(data);
			for (var i = 0; i < posts.length; i++) {
				post_info = posts[i];
				build_post_item(post_info).appendTo(main_container);
			}
		},
		error : function(xhr, statis, err) {
			if (_DEBUG) {
				log_debug("Create post failed " + err);
			}
		}
	});
}

function register_footer_call_back() {
	{
		var li_children = $("#footer").find("li");
		for (var idx = 0; idx < li_children.length; idx++) {
			li_children[idx].onclick = function(obj) {
				if (_DEBUG) {
					log_debug(sformat("Click on footer :{0}",
							obj.srcElement.innerText));
				}
				var src_li = obj.srcElement.parentNode("li");
				var ul = src_li.parentNode;
				for (var idx = 0; idx < ul.children.length; idx++) {
					ul.children[idx].className = "";
				}
				src_li.className = "active";
			}
		}
	}
}

/**
 * Entry for registering CALL-BACK on elements.
 */
function register_call_back() {
	register_footer_call_back();
}

function init() {
	primary_color_state = Math.floor(Math.random() * 1000);
	register_call_back();
}

function start_danmuku() {
	$(document).ready(function() {
		$("div[name=card_danmuku_content_container]").owlCarousel({
			navigation : true, // Show next and prev buttons
			slideSpeed : 300,
			paginationSpeed : 400,
			singleItem : true

		// "singleItem:true" is a shortcut for:
		// items : 1,
		// itemsDesktop : false,
		// itemsDesktopSmall : false,
		// itemsTablet: false,
		// itemsMobile : false
		});

	});
}

$(document).ready(function() {
	_DEBUG = false;
	_LOG_ON = false;
	init();
	if (_DEBUG) {
		var main_container = $("div[name=main-container]");
		for (var idx = 0; idx < 10; idx++) {
			var dummy_post_info = {
				"postID" : idx,
				"author" : "12345",
				"content" : "123123",
				"description" : "This is dummy post description",
				danmuku : [ {
					author : "dummy-danmuku-author",
					content : "dummy-danmuku-content",
					timestamp : "dummy-danmuku-timestamp"
				}, {
					author : "dummy-danmuku-author-2",
					content : "dummy-danmuku-content-2",
					timestamp : "dummy-danmuku-timestamp-2"
				} ]
			};
			build_post_item(dummy_post_info).appendTo(main_container);
		}
	} else {
		create_posts();
	}
	fill_backgroud_color_forall_card_content();
	start_danmuku();
});