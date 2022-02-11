<!DOCTYPE html>
<html style="min-height:100%" lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
    <link rel="stylesheet" type="text/css" href="{{ asset('/css/app.css') }}">
</head>
<body style = "background-image:url({{asset('/images/bg2.jpg')}});background-position:center;background-repeat:no-repeat;background-size:cover;height:100%">
    @include('inc.navbar_home_from_detail')
    <main role="main" class="container">
        <div style="margin:0 auto" class="jumbotron col-md-10">
            <div class="mb-4">
                <div class="form-label-group" style="float:left">
                    <div id="header" style="width: 400px; float:left;">
                        <div style="width:100%;height:50%;float:left">
                            <h1>List of menu for {{$choosen_merchant->Nama}}</h1>
                        </div>
                        <div style="left">
                            <h3>{{$choosen_merchant->Alamat}}</h3>
                        </div>
                    </div>
                </div>

                <div class ="form-label-group">
                    <div id='content' style="clear:left; background-color: white; opacity: 0.75; width: 100%; margin: auto; padding: 50px">
                        @if(isset($error))
                        <b style="color: red;">{{$error}}</b>
                        @endif
                        <form action="{{url('/search_menu')}}" method="post">
                        {{csrf_field()}}
                            <input type="text" name="NamaMenu" placeholder="Search Food">
                            <input type="submit" value="Search Menu">
                        </form>
                        @if(isset($menu))
                        @foreach($menu as $m)
                        <div style="margin-bottom: 20px">
                            <form action="{{url('/add_to_cart')}}" method="post">
                                {{csrf_field()}}
                                <img src="{{asset('/images/menu/'.$m->Id_menu.'.jpg')}}" alt="No Images" style="width: 25%; height: 25%;"></td>
                                <h3>{{$m->Nama}}</h3>
                                Rp:&nbsp{{$m->Harga}}
                                <input type="hidden" name="HiddenMenu" value="{{$m->Id_menu}}">
                                Jumlah:&nbsp<input type="text" name="jumlah">
                                <input type="submit" value="Add To Cart">
                            </form>
                        </div>
                        @endforeach
                        <form action="{{url('/checkout')}}" method="post">
                            {{csrf_field()}}
                            <input type="submit" value="Checkout">
                        </form>
                        @endif
                    </div>
                </div>
            </div>
        </div>
    </main>
</body>
</html>
