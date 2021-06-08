import firebase_admin
from firebase_admin import ml
from firebase_admin import credentials

firebase_admin.initialize_app(
    credentials.Certificate('[CREDENTIALS LOCATION/PATH]'),
    options={
        'storageBucket': '[CLOUD STORAGE LOCATION]' #cloud storage location
    }
)

model = ml.get_model("[MODEL_ID]") #put model id from deploying
source = ml.TFLiteModelSource.from_tflite_model_file('[MODEL LOCATION/PATH]')
model.model_format = ml.TFLiteFormat(model_source=source)

model.display_name = "[MODEL_NAME]" #do not change this name, changing this will require changes in the app also

model.tags = ["version_1"]
updated_model = ml.update_model(model)
ml.publish_model(updated_model.model_id)