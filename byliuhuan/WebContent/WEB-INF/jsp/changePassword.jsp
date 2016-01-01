<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
<script>
	function validateForm() {
		var origin = document.getElementById("origin_password").value;
		var new_pass = document.getElementById("new_password").value;
		var confirm_pass = document.getElementById("confirm_password").value;
		if (new_pass == confirm_pass)
			return true;
		else {
			alert("两次所述密码不一致");
			return false;
		}
	}

	function clearAllFields() {
		document.getElementById("origin_password").value = "";
		document.getElementById("new_password").value = "";
		document.getElementById("confirm_password").value = "";
	}
</script>
</head>

<body>
	<form name="myForm" action="/SpringWeb/addStudent"
		onsubmit="return validateForm()" method="post">
		<legend>修改密码</legend>
		<label>原密码</label><input type="password" id="origin_password" name="name" /></br> 
		<label>新密码</label><input type="password" id="new_password" name="age"/></br> 
		<label>确认密码</label><input type="password" id="confirm_password" name="id" /></br> 
		<input type="submit" value="提交"> <input type="button" value="重置" onclick="clearAllFields()">
	</form>
</body>

</html>