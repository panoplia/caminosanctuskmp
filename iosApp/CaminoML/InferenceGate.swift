import CoreML
import UIKit

// Gate enforces: background = ANE only, foreground = full pipeline.
// Prevents IOGPUMetalError crashes from Metal GPU in background state.
struct InferenceGate {

    static func predict(
        modelURL: URL,
        input: MLFeatureProvider
    ) throws -> MLFeatureProvider {
        let config = MLModelConfiguration()
        config.computeUnits = UIApplication.shared.applicationState == .active
            ? .all                   // foreground: CPU + ANE + Metal GPU
            : .cpuAndNeuralEngine    // background: ANE only, Metal GPU forbidden

        let model = try MLModel(contentsOf: modelURL, configuration: config)
        return try model.prediction(from: input)
    }
}
