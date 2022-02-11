<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
</head>
<body>
	<h1>Welcome to Ojek Online!</h1>
	<h3>Please enter your personal information below</h3>
	<hr><br>
    <form method='post' action='<?=site_url("mainpage/handle_login")?>'>
        Username:<input type='text' name='username'><br>
		Password:<input type='text' name='password'><br>
        <input type='submit' name='Login'>
    </form>
	<br><br>
	Belum punya akun? Registrasi disini:<form method='post' action='<?=site_url("login/handle_regis")?>'>
        <input type='submit' name='Registrasi' value='Registrasi'>
    </form>
</body>
</html>