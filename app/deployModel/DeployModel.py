import firebase_admin
from firebase_admin import ml
from firebase_admin import credentials

firebase_admin.initialize_app(
    credentials.Certificate('/private_key/service_account_key.json'),
    options={
        'storageBucket': 'trash-education.appspot.com' #cloud storage location
    }
)

source = ml.TFLiteGCSModelSource.from_tflite_model_file('file_name.tflite')
tflite_format = ml.TFLiteFormat(model_source=source)

model = ml.Model(
    display_name="filename", #name to download the model
    tags=["examples"],
    model_format=tflite_format
)

#Deploying model and publish it
new_model = ml.create_model(model)
ml.publish_model(new_model.model_id)
print(new_model.model_id)
print(new_model.display_name)
print(new_model.model_format)