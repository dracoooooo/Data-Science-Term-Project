from flask import Flask, jsonify, request
from flask_cors import CORS
import src.languange_classifier.code_language_classifier as clc
import src.code_recommender.code_recommender as cr


DEBUG = True

app = Flask(__name__)
app.config.from_object(__name__)

# enable CORS
CORS(app, resources={r'/*': {'origins': '*'}})


@app.route('/lang/', methods=['GET', 'POST'])
def lang():
    response_object = {'status': 'success'}
    if request.method == 'POST':
        post_data = request.get_json()
        code = post_data['code']
        print(code)
        lang, prob = cr.get_lang(code)
        return lang


@app.route('/func/', methods=['GET', 'POST'])
def func():
    response_object = {'status': 'success'}
    if request.method == 'POST':
        post_data = request.get_json()
        code = post_data['code']
        print(code)
        func, prob = cr.get_func(code)
        return lang


@app.route('/LandF/', methods=['GET', 'POST'])
def LandF():
    response_object = {'status': 'success'}
    if request.method == 'POST':
        post_data = request.get_json()
        code = post_data['code']
        print(code)
        lang, lang_prob = cr.get_lang(code)
        func, func_prob = cr.get_func(code)
        return {'lang': lang, 'func': func, 'lang_prob': str(lang_prob), 'func_prob': str(func_prob)}



if __name__ == "__main__":
    app.run()