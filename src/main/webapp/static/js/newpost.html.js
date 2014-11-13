function showStart() {
	alert('开始提交');
	return true;
}
function showSuccess() {
	alert("提交成功");
}

function newPostClick() {
	var form = $('new-post-form');
	var options = {
		beforeSubmit : showStart,
		success : showSuccess
	};
	form.submit(function() {
		$(this).ajaxSubmit(options);
		return false;
	});
}