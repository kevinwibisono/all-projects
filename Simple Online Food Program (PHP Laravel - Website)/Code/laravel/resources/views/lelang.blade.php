<!DOCTYPE html>
<html style="min-height:100%"  lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link rel="stylesheet" type="text/css" href="{{ asset('/css/app.css') }}">
    <title>Document</title>
</head>
<body style = "background-image:url({{asset('/images/bg2.jpg')}});background-position:center;background-repeat:no-repeat;background-size:cover;min-height:100%">
    @include('inc.navbar_home_user')
	<div id="container">
		<div id="heading" style="height: 100px">

		</div>
		<div id='content' style="clear:left; background-color: white; opacity: 0.75; width: 1000px; margin: auto; padding: 50px">
			@foreach($lelang as $l)
				<div style="margin-bottom: 25px">
                    <form action ="{{url('/enter_lelang')}}" method='post'>
                        {{csrf_field()}}
                        <h3>Lelang: {{$l->Nama_lelang}}</h3>
                        <hr>
                        Barang:
                        <img src="{{asset('/images/lelang/'.$l->Id_lelang.'.jpg')}}" alt="No Images" style="width: 10%; height: 10%;">
                        <b>{{$l->Barang}}</b>
                        <input type='submit' value='Enter'>
                        <input type='hidden' name='HiddenLelang' value="{{$l->Id_lelang}}">
                    </form>
                 </div>
			@endforeach
		</div>
	</div>
</body>
</html>
