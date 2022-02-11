<!DOCTYPE html>
<html style="height:100%" lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link rel="stylesheet" type="text/css" href="{{ asset('/css/app.css') }}">
    <title>Document</title>
	<script src="{{ asset('js/app.js') }}"></script>
	<meta id="token" name="token" content="{{ csrf_token() }}">

	<script>
		$.ajaxSetup({
			headers: {
				'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
			}
		});

		setInterval(function(){
			$.ajax({
				type:'get',
				url: "{{url('/get_bid_tertinggi')}}",
				data: '',
				success:function(data) {
					$("#text_bid_tertinggi").html("Bid tertinggi saat ini:" + data.msg);
					$("#bid_tertinggi").value(data.msg);
				},error:function(){

				}
			});
		}, 500);

		setInterval(function(){
			$.ajax({
				type:'get',
				url: "{{url('/get_count_down')}}",
				data: '',
				success:function(data) {
					$("#text_count_down").html("Waktu tersisa :" +data.count_down);
				},error:function(){

				}
			});
		}, 500);

		function insert_bid(){
			var bid = $("#bid_user").val();
			alert(bid);
			$.ajax({
				type:'get',
				url: "{{url('/insert_bid')}}",
				data:{bid:bid},
				success:function(data) {
					alert(data.pesan_insert);
				}
			});
		}
      </script>
</head>
<body style = "background-image:url({{asset('/images/bg2.jpg')}});background-position:center;background-repeat:no-repeat;background-size:cover;height:100%">
	@include('inc.navbar_home_user')
	<br>
	<main role="main" class="container">
		<div style="float:none;margin:0 auto;background-color:grey; opacity:0.9" class="jumbotron col-md-6">
			<div class="text-center mb-4">
				<div class="form-label-group">
                    <img src="{{asset('/images/lelang/'.$lelang->Id_lelang.'.jpg')}}" alt="No Images" style="width: 50%; height: 50%;">
					<div style="width: 100%; float:left;">
							<h1>Halaman bidding untuk {{$lelang->Barang}}</h1>
							<h3 id = 'text_bid_tertinggi'>Bid tertinggi saat ini:{{$lelang->Current_bid}}</h3>
							<h3 id = 'text_count_down'></h3>
					</div>
				</div>
				<div class ="form-label-group">
					<hr>
					Harga yang ingin anda bid:<input type='number' min="1000" value="1000" step="1000" id="bid_user" name='bid'><br><br>
					<input type='button' class="btn btn-primary" value="Submit" onclick="insert_bid()">
					<input type='hidden' name='HiddenMerchant' value="{{$lelang->Id_lelang}}">
				</div>
			</div>
		</div>
	</main>
    @include('inc.footer')
</body>
</html>
