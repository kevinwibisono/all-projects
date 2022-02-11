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

    <main style="margin-top:100px" role="main" class="container">
        <div style="float:none;margin:0 auto;background-color:grey; opacity:0.9" class="jumbotron col-md-6">
            <div class="text-center mb-4">
                <div class="form-label-group">
                    <div id="header">
                        <div>
                            <h3>Your balance is Rp.{{$current_user->Saldo}}</h3>
                        </div>
                    </div>
                </div>

                <div class="form-label-group">
                    <div id="header">
                        <div>
                        <form action="{{url('/handle_topup')}}" method='post'>
                            {{csrf_field()}}
                            <input placeholder="Enter a number" name="input_saldo" required type="number" value="" min="-100" max="100000"/>
                            <input class="btn btn-lg btn-primary btn-block" type="submit" name="btn_topup" value="Confirm" />
                        </form>
                        </div>
                    </div>
                </div>
                <br>
                <br>
            </div>

            <div >
                <br>
                <form action="{{url('/to_home_customer')}}" method='post'>
                    {{csrf_field()}}
                    <input class="btn btn-block btn-lg btn-primary"style="margin-top:0px"type="submit" value='Back'>
                </form>
            </div>
        </div>
	</main>
    @include('inc.footer')
</body>
</html>