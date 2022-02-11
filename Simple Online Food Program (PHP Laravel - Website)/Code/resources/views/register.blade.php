<!DOCTYPE html>
<html style="min-height:100%" lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link rel="stylesheet" type="text/css" href="{{ asset('/css/app.css') }}">
    <title>Document</title>
</head>
<body style = "background-image:url({{asset('/images/bg2.jpg')}});background-position:center;background-repeat:no-repeat;background-size:cover;min-height:100%">
	@include('inc.navbar_register')
	<br>
	<main role="main" class="container">
		<div style="float:none;margin:0 auto;background-color:grey; opacity:0.9" class="jumbotron col-md-6">
			<div class="text-center mb-4">
				<div class="text-center form-label-group">
					<div style="width: 100%; float: left;">
						<h1>&nbspHalaman Registrasi User</h1>
						<h3>&nbsp&nbspSilahkan masukkan data diri anda</h3>
					</div>
				</div>

				<div class="text-center form-label-group">
					<hr>
					<form style="display:table;margin:auto"action="{{url('/handle_register')}}" method="post" enctype="multipart/form-data">
						{{csrf_field()}}
						<label style="display:table-row">Username:</label> <input type='text' name='username'><br>
						@if(isset($error))
							@if($error->has('username'))
								<b style='color:red;'>{{$error->first('username')}}</b><br>
							@endif
						@endif
						<br>
						<label style="display:table-row">Password:</label> <input type='password' name='password'><br>
						@if(isset($error))
							@if($error->has('password'))
								<b style='color:red;'>{{$error->first('password')}}</b><br>
							@endif
						@endif
						<br>
						<label style="display:table-row">Nama:</label><input type='text' name='nama'><br>
						@if(isset($error))
							@if($error->has('nama'))
								<b style='color:red;'>{{$error->first('nama')}}</b><br>
							@endif
						@endif
						<br>
						<label style="display:table-row"> Alamat: </label> <input type='text' name='alamat'><br>
						@if(isset($error))
							@if($error->has('alamat'))
								<b style='color:red;'>{{$error->first('alamat')}}</b><br>
							@endif
						@endif
						<br>
						<label style="display:table-row"> Role: </label> <input type="radio" name='role' value='Pelanggan' checked>Pelanggan
						<input type="radio" name='role' value='Penjual'>Penjual<br>
                        <br>
                        <label style="display:table-row"> Profile Image: </label> <input type="file" name="profileimg">
                        <br>
                        @if(isset($error))
                            @if($error->has('profileimg'))
                                <b style='color:red;'>{{$error->first('profileimg')}}</b><br>
                            @endif
                        @endif
                        <br>
						<input class="btn btn-dark btn-block"type='submit' value='Register'>
					</form>
				</div>
			</div>
		</div>
	</main>
</body>
</html>
