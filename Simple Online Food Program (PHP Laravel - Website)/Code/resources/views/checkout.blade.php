<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
    <link rel="stylesheet" type="text/css" href="{{ asset('/css/app.css') }}">
</head>
<body style = "background-image:url({{asset('/images/bg2.jpg')}});background-position:center;background-repeat:no-repeat;background-size:cover;height:100%">
    <script>
        function cekBerhasil(){
            var total = document.getElementById("HiddenTotal").value;
            if(parseInt({{$jumwaiting}}) > 0){
                alert("Gagal! Anda masih memiliki transaksi yang belum selesai");
                alert("Membatalkan transaksi...");
            }
            else{
                if(parseInt({{$current_user->Saldo}}) >= parseInt(total)){
                    alert("Berhasil");
                }
                else{
                    alert("Gagal! Saldo tidak cukup");
                    alert("Membatalkan transaksi...");
                }
            }
        }
    </script>
    @include('inc.navbar_home_from_detail')
    <main role="main" class="container">
        <div style="margin:0 auto" class="jumbotron col-md-10">
            <div class="mb-4">
                <div class="form-label-group" style="float:left">
                    <div id="header" style="width: 800px; float:left;">
                        <div style="width:100%;height:50%;float:left">
                            <h1>Pembayaran</h1>
                        </div>
                    </div>
                </div>

                <div class ="form-label-group">
                    <div id='content' style="clear:left; background-color: white; opacity: 0.75; width: 100%; margin: auto; padding: 50px">
                        <input type="hidden" id="HiddenWaiting" value="{{$jumwaiting}}">
                        Alamat Pengiriman: {{$current_user->Alamat}}
                        <h3>Berikut adalah daftar keranjang anda</h3>
                        <table>
                            <thead>
                                <th>Gambar</th>
                                <th>Nama Menu</th>
                                <th>Harga Menu</th>
                                <th>Jumlah Menu</th>
                            </thead>
                        <?php $total = 0; ?>
                        @foreach($keranjang as $k)
                            <tr>
                                <td style="width: 20%;"><img src="{{asset('/images/menu/'.$k->Id_menu.'.jpg')}}" alt="No Images" style="width: 100%; height: 20%;"></td></td>
                                <td>{{$k->Nama_menu}}</td>
                                <td>{{$k->Harga_menu}}</td>
                                <td>{{$k->Jumlah_menu}}</td>
                                <td>
                                    <form action="{{url('/dec_keranjang')}}" method="post">
                                    {{csrf_field()}}
                                        <input type="hidden" name="HiddenCart" value="{{$k->Id_cart}}">
                                        <input type="submit" value="-">
                                    </form>
                                    <form action="{{url('/inc_keranjang')}}" method="post">
                                    {{csrf_field()}}
                                        <input type="hidden" name="HiddenCart" value="{{$k->Id_cart}}">
                                        <input type="submit" value="+">
                                    </form>
                                </td>
                            </tr>
                            <?php $total += ($k->Harga_menu*$k->Jumlah_menu);?>
                        @endforeach
                        <tr>
                            <td>Total</td>
                            <td></td>
                            <td>Rp&nbsp<?php echo $total;?></td>
                        </tr>
                        </table>
                        <form action="{{url('/do_transaction')}}" method="post">
                            {{csrf_field()}}
                            <input type="hidden" name="HiddenTotal" id="HiddenTotal" value="<?php echo $total;?>">
                            <input type="submit" value="Buy" onclick="cekBerhasil()">
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </main>

</body>
</html>
