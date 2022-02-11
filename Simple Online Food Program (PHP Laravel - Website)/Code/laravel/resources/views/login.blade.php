<!DOCTYPE html>
<html style="min-height:100%" lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link rel="stylesheet" type="text/css" href="{{ asset('/css/app.css') }}">
    <title>Document</title>
</head>
<body style = "background-image:url({{asset('/images/bg2.jpg')}});background-position:center;background-repeat:no-repeat;background-size:cover;height:100%">
	@include('inc.navbar')	
	<br>
	<main style="position:relative" role="main" class="container">
		<div style="float:none;margin:0 auto;background-color:grey; opacity:0.9" class="jumbotron col-md-6">
			<div class="text-center mb-4">
				<div class="form-label-group">
					<div style="width: 100%; float:left;">
							<h1>&nbspHalaman Login User</h1>
							<h3>&nbsp&nbspSilahkan masukkan data diri anda'</h3>
					</div>
				</div>
				<div class ="form-label-group">
					<hr>
					@if(isset($error))
						<b style='color:red;'>{{$error}}</b>
					@endif
					<form style="display:table;margin:auto"action="{{url('/handle_login')}}" method="post">
						{{csrf_field()}}
						<label style="display:table-row"> Username: </label> <input type='text' name='username'><br><br>
						<label style="display:table-row"> Password: </label> <input type='password' name='password'><br><br>
						<input type='submit' class="btn btn-dark btn-block" value="login">
					</form>
				</div>
			</div>
		</div>
	</main>
	@include('inc.footer')	
</body>
</html>