function showStart() {
	alert('开始提交');
	return true;
}
function showSuccess() {
	alert("提交成功");
}

function newPostSubmit() {
	var form = $('new-post-form');
	var options = {
		beforeSubmit : showStart,
		success : showSuccess
	};
	form.submit(function(data, textStatus, jqXHR) {
		alert(data);
		alert(textStatus);
		alert(jqHXR);
	});
}