import logging

from alexa.ask_sdk_core.skill_builder import SkillBuilder
from alexa.ask_sdk_core.handler_input import HandlerInput
from alexa.ask_sdk_core.dispatch_components import (
    AbstractRequestHandler,
    AbstractExceptionHandler,
    AbstractResponseInterceptor,
    AbstractRequestInterceptor)
from alexa.ask_sdk_core.utils import is_intent_name, is_request_type
from alexa.ask_sdk_core.response_helper import (
    get_plain_text_content,
    get_rich_text_content)
from alexa.ask_sdk_core.utils.request_util import get_user_id
from alexa.ask_sdk_model import ui, Response

sb = SkillBuilder()

logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)

class LaunchRequestHandler(AbstractRequestHandler):
    def can_handle(self, handler_input):
        return is_request_type("LaunchRequest")(handler_input)

    def handle(self, handler_input):
        # type: (HandlerInput) -> Response

        logger.info("In LaunchRequestHandler")
        # TODO: get userId and look up in DB
        handler_input.response_builder.speak("Hello")
        return handler_input.response_builder.response

sb.add_request_handler(LaunchRequestHandler())

lambda_handler = sb.lambda_handler()