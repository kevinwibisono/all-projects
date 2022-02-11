<!DOCTYPE html>
<html style="height:100%" lang="en">
  <head>
    <meta charset="utf-8">
    <title>REPORT MERCHANT</title>
    <link rel="stylesheet" type="text/css" href="{{ asset('/css/app.css') }}">
  </head>
  <body  style = "background-image:url({{asset('/images/bg2.jpg')}});background-position:center;background-repeat:no-repeat;background-size:cover;height:100%">
    @include('inc.navbar_home_user')

    <main role="main" class="container">
		<div style="float:none;margin:auto;background-color:grey; opacity:0.9" class="jumbotron col-md-6">
            <form action ="{{url('/report_merchant')}}" method='post' enctype="multipart/form-data">
                {{csrf_field()}}
                Merchant Name : <input type="text" name="nama_merchant" value="{{$merchant->Nama}}" disabled>
                <br>
                Message
                <br>
                <textarea rows="4" cols="50" name="message"></textarea>
                @if(isset($error))
                  @if($error->has('message'))
                    <b style='color:red;'>{{$error->first('message')}}</b><br>
                  @endif
                @endif
                <br>

                Proof Photo : <input type="file" name="proof">
                @if(isset($error))
                    @if($error->has('proof'))
                        <b style='color:red;'>{{$error->first('proof')}}</b><br>
                    @endif
                @endif
                <br>
                <br>
                <input class="btn btn-dark btn-block" type="submit" value="Report Merchant">
                <input type='hidden' name='HiddenMerchant' value="{{$merchant->Username}}">
            </form>
		</div>
	</main>
    @include('inc.footer')
  </body>
</html>
