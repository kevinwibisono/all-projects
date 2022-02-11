<!DOCTYPE html>
<html style="height:100%" lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link rel="stylesheet" type="text/css" href="{{ asset('/css/app.css') }}">
    <title>Document</title>
</head>
<body style = "background-image:url({{asset('/images/bg2.jpg')}});background-position:center;background-repeat:no-repeat;background-size:cover;height:100%">
    @include('inc.navbar_home_user')

    <main role="main" class="container">
		<div class="mb-4">
			<div class="form-label-group" style="float:left">
                <div id="header" style="width: 800px; float:left;">
                    <div style="width:100%;height:50%;float:left">
                        <form action="{{url('/profile')}}" method="post">
                            {{csrf_field()}}
                            <h1 style="float:left;color:white">Welcome ,<input style="font-size :30px;margin-bottom:5px;color:white"type="submit" class="btn btn-link" value="{{$current_user->Nama}}"name="submit"></h1>
                        </form>
                    </div>
                    <div style="left">
                        <h3 style="color:white">Your balance is Rp.{{$current_user->Saldo}}</h3>
                    </div>
                </div>
                <div style="text-align: right; width: 250px; float: left;">
                    <form action="{{url('/to_topup')}}" method='post'>
                        {{csrf_field()}}
                        <input class="btn btn-primary"style="margin-top:20px"type="submit" value='Top Up'>
                    </form>
                </div>
			</div>

			<div class ="form-label-group">
                <div id='content' style="clear:left; background-color: white; opacity: 0.75; width: 1000px; margin: auto; padding: 50px">
                    @foreach($penjual as $p)
                        <div>
                            <form action ="{{url('/to_detail')}}" method='post'>
                                {{csrf_field()}}
                                <h3 style="clear:both;">{{$p->Nama}}</h3>
                                <b style="float:left; margin:10px">{{$p->Alamat}}</b>
                                <input class="btn btn-secondary btn-lg"type='submit' value='Telusuri'style="float:left; margin-right: 10px;">
                                <input type='hidden' name='HiddenMerchant' value="{{$p->Username}}">
                            </form>
                            <form action ="{{url('/to_report_merchant')}}" method='post'>
                                {{csrf_field()}}
                                <input class="btn btn-secondary btn-lg" type="submit" value="Report Merchant">
                                <input type='hidden' name='HiddenMerchant' value="{{$p->Username}}">
                            </form>
                        </div>
                        <br>
                        <div style="border-bottom: 1px solid black;"></div>
                        <br>
                    @endforeach
                </div>
			</div>
		</div>
	</main>
    @include('inc.footer')
</body>
</html>
